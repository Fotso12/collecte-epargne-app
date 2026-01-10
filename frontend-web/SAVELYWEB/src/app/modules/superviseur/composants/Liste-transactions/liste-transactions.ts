import { Component, OnInit, ChangeDetectorRef, OnDestroy, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin, Subscription } from 'rxjs';
import { StatutTransaction } from '../../../../donnees/modeles/enums';
import { TransactionService } from '../../../../core/services/transaction.service';
import { EmployeService } from '../../../../core/services/employe.service';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-liste-transactions',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './liste-transactions.html',
  styleUrls: ['./liste-transactions.css']
})
export class ListeTransactionsComponent implements OnInit, OnDestroy {
  // --- Propriétés originales préservées ---
  transactions: any[] = [];
  transactionsFiltrees: any[] = [];
  employes: any[] = [];
  user: any = null;
  private authSub?: Subscription;

  stats = { total: 0, validees: 0, pourcentage: 0 };
  filtreReference = '';
  filtreStatut = '';
  filtreType = '';

  chargement = false;
  isDetailsModalOpen = false;
  transactionSelectionnee?: any;
  StatutTx = StatutTransaction;

  // --- Nouveaux états pour les modals améliorés ---
  isConfirmModalOpen = false;
  isRejectModalOpen = false;
  motifRejetTexte = '';

  constructor(
    private transactionService: TransactionService,
    private employeService: EmployeService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private zone: NgZone
  ) { }

  ngOnInit(): void {
    this.recupererUtilisateur();
    if (this.authService?.currentUser$) {
      this.authSub = this.authService.currentUser$.subscribe(userData => {
        if (userData) {
          this.zone.run(() => {
            this.user = userData;
            if (this.transactions.length === 0) this.chargerDonnees();
          });
        }
      });
    }
    this.chargerDonnees();
  }

  ngOnDestroy(): void {
    if (this.authSub) this.authSub.unsubscribe();
  }

  private recupererUtilisateur() {
    try {
      this.user = this.authService.getUser() || JSON.parse(localStorage.getItem('auth_user') || 'null');
    } catch (e) {
      console.warn("Session utilisateur non trouvée au démarrage.");
    }
  }

  chargerDonnees = () => {
    if (this.chargement) return;
    this.chargement = true;
    this.forcerRendu();

    forkJoin({
      txs: this.transactionService.getAll(),
      emps: this.employeService.getAll()
    }).subscribe({
      next: (resultat) => {
        this.zone.run(() => {
          this.employes = resultat.emps || [];
          this.transactions = (resultat.txs || []).map(t => ({
            ...t,
            nomInitiateur: this.trouverNomComplet(t.idEmployeInitiateur),
            nomCaissier: this.trouverNomComplet(t.idCaissierValidateur)
          }));

          this.transactionsFiltrees = [...this.transactions];
          this.calculerStats();
          this.chargement = false;
          this.forcerRendu();
        });
      },
      error: (err) => {
        this.zone.run(() => {
          console.error('Erreur de chargement:', err);
          this.chargement = false;
          this.forcerRendu();
        });
      }
    });
  }

  private forcerRendu() {
    this.cdr.markForCheck();
    this.cdr.detectChanges();
    setTimeout(() => {
      this.cdr.detectChanges();
    }, 0);
  }

  private trouverNomComplet(idRecu?: any): string {
    if (!idRecu || !this.employes || this.employes.length === 0) return 'Non assigné';
    const idStr = String(idRecu).trim().toLowerCase();
    const emp = this.employes.find(e =>
      String(e.idEmploye) === idStr ||
      e.matricule?.toString().toLowerCase() === idStr ||
      e.loginUtilisateur?.toString().toLowerCase() === idStr
    );
    return emp ? `${emp.prenom} ${emp.nom}` : idRecu;
  }

  // --- Logique de validation avec Modal ---
  validerTransaction(id: string) {
    this.transactionSelectionnee = this.transactions.find(t => t.idTransaction === id);
    this.isConfirmModalOpen = true;
  }

  confirmerValidation() {
    const loginValide = this.user?.login || this.user?.email || JSON.parse(localStorage.getItem('auth_user') || '{}')?.login;
    if (!loginValide || !this.transactionSelectionnee) {
      alert("Erreur : Identifiant introuvable.");
      return;
    }

    this.transactionService.validerParSuperviseur(this.transactionSelectionnee.idTransaction, loginValide).subscribe({
      next: () => {
        this.zone.run(() => {
          this.isConfirmModalOpen = false;
          this.isDetailsModalOpen = false;
          // Recharger immédiatement pour voir VALIDEE_SUPERVISEUR
          this.chargerDonnees();

          // Auto-refresh après 2 secondes pour voir le passage à TERMINEE
          setTimeout(() => {
            this.chargerDonnees();
          }, 2000);
        });
      },
      error: (err) => alert("Erreur : " + (err.error?.message || "Serveur injoignable"))
    });
  }

  // --- Logique de rejet avec Modal Amélioré ---
  rejeterTransaction(id: string) {
    this.transactionSelectionnee = this.transactions.find(t => t.idTransaction === id);
    this.motifRejetTexte = '';
    this.isRejectModalOpen = true;
  }

  confirmerRejet() {
    if (!this.motifRejetTexte.trim()) return;

    this.transactionService.rejeter(this.transactionSelectionnee.idTransaction, this.motifRejetTexte).subscribe({
      next: () => {
        this.zone.run(() => {
          this.isRejectModalOpen = false;
          this.isDetailsModalOpen = false;
          this.chargerDonnees();
        });
      },
      error: (err) => alert("Erreur lors du rejet")
    });
  }

  // --- Fonctions utilitaires préservées ---
  calculerStats() {
    this.stats.total = this.transactions.length;
    this.stats.validees = this.transactions.filter(t =>
      t.statut === 'TERMINEE' || t.statut === 'VALIDEE_SUPERVISEUR'
    ).length;
    this.stats.pourcentage = this.stats.total > 0 ? (this.stats.validees / this.stats.total) * 100 : 0;
  }

  appliquerFiltres() {
    const search = this.filtreReference.toLowerCase();
    this.transactionsFiltrees = this.transactions.filter(t =>
      (t.reference?.toLowerCase().includes(search) || t.nomInitiateur?.toLowerCase().includes(search)) &&
      (!this.filtreStatut || t.statut === this.filtreStatut) &&
      (!this.filtreType || t.typeTransaction === this.filtreType)
    );
  }

  chargerTransactions() { this.chargerDonnees(); }
  voirDetails(t: any) { this.transactionSelectionnee = t; this.isDetailsModalOpen = true; }

  fermerModal() {
    this.isDetailsModalOpen = false;
    this.isConfirmModalOpen = false;
    this.isRejectModalOpen = false;
  }
}