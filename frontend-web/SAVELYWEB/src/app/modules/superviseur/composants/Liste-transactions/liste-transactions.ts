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

  constructor(
    private transactionService: TransactionService,
    private employeService: EmployeService,
    private authService: AuthService,
    private cdr: ChangeDetectorRef,
    private zone: NgZone // Injecté pour forcer le cycle de rendu Angular
  ) {}

  ngOnInit(): void {
    // 1. Charger l'utilisateur immédiatement
    this.recupererUtilisateur();

    // 2. S'abonner aux changements d'utilisateur
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

    // 3. Charger les transactions
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
    this.forcerRendu(); // Affiche le spinner

    forkJoin({
      txs: this.transactionService.getAll(),
      emps: this.employeService.getAll()
    }).subscribe({
      next: (resultat) => {
        // Exécution dans la zone Angular pour garantir la mise à jour immédiate de la vue
        this.zone.run(() => {
          this.employes = resultat.emps || [];
          
          // Création d'une nouvelle référence de tableau (important pour la détection)
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
    // Double sécurité pour les cas asynchrones complexes
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

  validerTransaction(id: string) {
    const loginValide = this.user?.login || this.user?.email || JSON.parse(localStorage.getItem('auth_user') || '{}')?.login;
    if (!loginValide) {
      alert("Erreur : Identifiant introuvable. Reconnectez-vous.");
      return;
    }

    if (confirm(`Confirmer la validation par ${loginValide} ?`)) {
      this.transactionService.validerParSuperviseur(id, loginValide).subscribe({
        next: () => {
          this.zone.run(() => {
            alert("Transaction validée !");
            this.isDetailsModalOpen = false;
            this.chargerDonnees();
          });
        },
        error: (err) => alert("Erreur : " + (err.error?.message || "Serveur injoignable"))
      });
    }
  }

  rejeterTransaction(id: string) {
    const motif = prompt('Motif du rejet :');
    if (motif?.trim()) {
      this.transactionService.rejeter(id, motif).subscribe({
        next: () => {
          this.zone.run(() => {
            this.isDetailsModalOpen = false;
            this.chargerDonnees();
          });
        }
      });
    }
  }

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
  fermerModal() { this.isDetailsModalOpen = false; }
}