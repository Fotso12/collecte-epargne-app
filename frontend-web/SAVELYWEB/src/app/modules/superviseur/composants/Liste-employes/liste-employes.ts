import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { switchMap, catchError, of } from 'rxjs'; 

import { EmployeDto, TypeEmploye } from '../../../../donnees/modeles/employe.modele';
import { EmployeService } from '../../../../core/services/employe.service';
import { UtilisateurService } from '../../../../core/services/utilisateur.service';
import { StatutGenerique } from '../../../../donnees/modeles/enums';

@Component({
  selector: 'app-liste-employes',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './liste-employes.html',
  styleUrls: ['./liste-employes.css']
})
export class ListeEmployesComponent implements OnInit {
  employes: EmployeDto[] = [];
  employesFiltres: EmployeDto[] = [];
  typeActuel: TypeEmploye = TypeEmploye.CAISSIER;
  titrePage = '';
  filtreMatricule = '';
  listeStatuts = Object.values(StatutGenerique);

  isModalOpen = false;
  isAddModalOpen = false;
  isEditMode = false;
  
  employeSelectionne: any = null;
  employeForm!: FormGroup;

  confirmStatut = { show: false, employe: null as any, nouveauStatut: '' };
  confirmSuppression = { show: false, matricule: '', nom: '', login: '' };
  feedbackModal = { show: false, isError: false, message: '' };

  // --- Propriétés Clients ---
  isClientsModalOpen = false;
  tousLesClientsDuCollecteur: any[] = [];
  clientsAffiches: any[] = [];
  pageActuelleClient = 1;
  taillePageClient = 10;
  collecteurActif: any = null;

  constructor(
    private employeService: EmployeService,
    private utilisateurService: UtilisateurService,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef,
    private fb: FormBuilder
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.route.url.subscribe(() => {
      const path = this.route.snapshot.url.at(-1)?.path || '';
      this.typeActuel = path === 'collecteurs' ? TypeEmploye.COLLECTEUR : TypeEmploye.CAISSIER;
      this.titrePage = (this.typeActuel === TypeEmploye.COLLECTEUR) ? 'LISTE DES COLLECTEURS' : 'LISTE DES CAISSIERS';
      this.chargerDonnees();
    });
  }

  initForm() {
    this.employeForm = this.fb.group({
      login: ['', Validators.required],
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      telephone: ['', Validators.required],
      password: [''], 
      dateEmbauche: [new Date().toISOString().split('T')[0], Validators.required],
      commissionTaux: [0],
      statut: [StatutGenerique.ACTIF]
    });
  }

  chargerDonnees() {
    const serviceCall = (this.typeActuel === TypeEmploye.COLLECTEUR) 
      ? this.employeService.getCollecteurs() : this.employeService.getCaissiers();

    serviceCall.subscribe({
      next: (data) => {
        this.employes = data.map(e => ({
          ...e,
          statut: e.statut || (e as any).utilisateur?.statut || 'ACTIF'
        }));
        this.appliquerFiltres();
        this.cdr.detectChanges();
      },
      error: () => this.showFeedback('Erreur de chargement', true)
    });
  }

  appliquerFiltres() {
    const search = this.filtreMatricule.toLowerCase();
    this.employesFiltres = this.employes.filter(e =>
      e.matricule?.toLowerCase().includes(search) ||
      e.nom?.toLowerCase().includes(search) || e.prenom?.toLowerCase().includes(search)
    );
  }

  ouvrirListeClients(collecteur: any) {
    // Sécurité : Si le matricule est absent, on ne lance pas la requête (évite l'erreur 400)
    if (!collecteur || !collecteur.matricule) {
      console.error("Matricule manquant pour le collecteur:", collecteur);
      this.showFeedback('Erreur: Matricule introuvable', true);
      return;
    }

    this.collecteurActif = collecteur;
    this.pageActuelleClient = 1;
    this.employeService.getClientsByCollecteur(collecteur.matricule).subscribe({
      next: (data) => {
        this.tousLesClientsDuCollecteur = data;
        this.mettreAJourTrancheClients();
        this.isClientsModalOpen = true;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error("Erreur chargement clients:", err);
        this.showFeedback('Erreur lors du chargement des clients', true);
      }
    });
  }

  mettreAJourTrancheClients() {
    const debut = (this.pageActuelleClient - 1) * this.taillePageClient;
    const fin = debut + this.taillePageClient;
    this.clientsAffiches = this.tousLesClientsDuCollecteur.slice(debut, fin);
  }

  changerPageClient(direction: number) {
    this.pageActuelleClient += direction;
    this.mettreAJourTrancheClients();
  }

  get totalPagesClient(): number {
    return Math.ceil(this.tousLesClientsDuCollecteur.length / this.taillePageClient);
  }

  fermerModalClients() {
    this.isClientsModalOpen = false;
  }

  enregistrer() {
    if (this.employeForm.invalid) return;
    const val = this.employeForm.getRawValue();

    if (this.isEditMode && this.employeSelectionne) {
      const payloadModif = { ...this.employeSelectionne, ...val };
      delete (payloadModif as any).password;

      this.employeService.modifierEmploye(this.employeSelectionne.matricule!, payloadModif).pipe(
        catchError(err => {
          console.error('Erreur Update:', err);
          return of(null);
        })
      ).subscribe(res => {
        if (res) {
          this.showFeedback('Modifications enregistrées avec succès');
          this.fermerModalAjout();
          this.chargerDonnees();
        } else {
          this.showFeedback('Erreur lors de la modification', true);
        }
      });

    } else {
      const roleId = this.typeActuel === TypeEmploye.CAISSIER ? 2 : 3;
      this.utilisateurService.enregistrerUtilisateur({ ...val, idRole: roleId }).pipe(
        switchMap(() => this.employeService.enregistrerEmploye({
            ...val,
            typeEmploye: this.typeActuel,
            loginUtilisateur: val.login,
            idAgence: 1
          } as any))
      ).subscribe({
        next: () => {
          this.showFeedback('Compte créé avec succès !');
          this.fermerModalAjout();
          this.chargerDonnees();
        },
        error: () => this.showFeedback('Erreur de création', true)
      });
    }
  }

  ouvrirConfirmationStatut(e: EmployeDto, st: string) {
    if (e.statut === st) return;
    this.confirmStatut = { show: true, employe: e, nouveauStatut: st };
    this.cdr.detectChanges();
  }

  confirmerChangementStatut() {
    const emp = this.confirmStatut.employe;
    const nouveau = this.confirmStatut.nouveauStatut;
    this.utilisateurService.modifierStatut(emp.loginUtilisateur!, nouveau).subscribe({
      next: () => {
        emp.statut = nouveau; 
        this.showFeedback(`Statut mis à jour`);
        this.confirmStatut.show = false;
        this.cdr.detectChanges();
      },
      error: () => this.showFeedback('Erreur', true)
    });
  }

  getStatutBtnClass(actuel: any, btn: string) {
    if (!actuel) return 'btn-light text-muted opacity-50';
    const sActuel = actuel.toString().toUpperCase();
    const sBtn = btn.toUpperCase();
    if (sActuel !== sBtn) return 'btn-light text-muted opacity-50';
    switch(sActuel) {
      case 'ACTIF': return 'btn-success text-white fw-bold shadow-sm';
      case 'INACTIF': return 'btn-danger text-white fw-bold shadow-sm';
      case 'SUSPENDU': return 'btn-warning text-dark fw-bold shadow-sm';
      default: return 'btn-secondary text-white';
    }
  }

  ouvrirConfirmationSuppression(e: EmployeDto) {
    this.confirmSuppression = { 
      show: true, 
      matricule: e.matricule!, 
      nom: `${e.nom} ${e.prenom}`,
      login: e.loginUtilisateur!
    };
    this.cdr.detectChanges();
  }

  confirmerSuppression() {
    if (!this.confirmSuppression.matricule) return;

    this.employeService.supprimerEmploye(this.confirmSuppression.matricule).pipe(
      switchMap(() => {
        if (this.confirmSuppression.login) {
          return this.utilisateurService.supprimerUtilisateur(this.confirmSuppression.login).pipe(
            catchError(() => of('Cascade déjà gérée par le backend'))
          );
        }
        return of(null);
      }),
      catchError(err => {
        console.error('Erreur suppression:', err);
        return of(null);
      })
    ).subscribe({
      next: (res) => {
        if (res !== null) {
          this.showFeedback('Suppression effectuée avec succès');
        } else {
          this.showFeedback('Erreur lors de la suppression', true);
        }
        this.confirmSuppression.show = false;
        this.chargerDonnees();
      }
    });
  }

  ouvrirModalAjout() { 
    this.isEditMode = false; 
    this.employeForm.reset({
      dateEmbauche: new Date().toISOString().split('T')[0],
      commissionTaux: 0, statut: StatutGenerique.ACTIF
    });
    this.employeForm.get('login')?.enable();
    this.isAddModalOpen = true; 
  }

  ouvrirModalModification(e: any) { 
    this.isEditMode = true; 
    this.employeSelectionne = { ...e };
    this.employeForm.patchValue({
      login: e.loginUtilisateur || e.login, 
      nom: e.nom, 
      prenom: e.prenom,
      email: e.email, 
      telephone: e.telephone,
      dateEmbauche: e.dateEmbauche, 
      commissionTaux: e.commissionTaux, 
      statut: e.statut
    });
    this.employeForm.get('login')?.disable();
    this.isAddModalOpen = true; 
  }

  voirDetails(e: any) { 
    this.employeSelectionne = { ...e }; 
    this.isModalOpen = true; 
    this.cdr.detectChanges();
  }

  fermerModal() { this.isModalOpen = false; }
  fermerModalAjout() { this.isAddModalOpen = false; }

  showFeedback(m: string, err = false) { 
    this.feedbackModal = {show: true, message: m, isError: err}; 
    setTimeout(() => { this.feedbackModal.show = false; this.cdr.detectChanges(); }, 3000); 
  }
}