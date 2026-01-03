import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { switchMap } from 'rxjs'; 

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
  employeSelectionne: EmployeDto | null = null;
  employeForm!: FormGroup;

  confirmStatut = { show: false, employe: null as any, nouveauStatut: '' };
  feedbackModal = { show: false, isError: false, message: '' };

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
      this.appliquerValidateurs();
    });
  }

  initForm() {
    this.employeForm = this.fb.group({
      login: ['', Validators.required],
      nom: ['', Validators.required],
      prenom: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      telephone: ['', Validators.required],
      password: ['', [Validators.required, Validators.minLength(6)]],
      dateEmbauche: [new Date().toISOString().split('T')[0], Validators.required],
      commissionTaux: [0],
      statut: [StatutGenerique.ACTIF]
    });
  }

  appliquerValidateurs() {
    const comm = this.employeForm.get('commissionTaux');
    if (this.typeActuel === TypeEmploye.COLLECTEUR) {
      comm?.setValidators([Validators.required, Validators.min(0)]);
    } else {
      comm?.clearValidators();
      comm?.setValue(0);
    }
    comm?.updateValueAndValidity();
  }

  chargerDonnees() {
    const serviceCall = (this.typeActuel === TypeEmploye.COLLECTEUR) 
      ? this.employeService.getCollecteurs() 
      : this.employeService.getCaissiers();

    serviceCall.subscribe({
      next: (data) => {
        this.employes = data.map(e => ({
          ...e,
          statut: e.statut || (e as any).utilisateur?.statut || 'ACTIF'
        }));
        this.appliquerFiltres();
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur chargement:', err);
        this.showFeedback('Erreur lors de la récupération des données', true);
      }
    });
  }

  appliquerFiltres() {
    const search = this.filtreMatricule.toLowerCase();
    this.employesFiltres = this.employes.filter(e =>
      e.matricule?.toLowerCase().includes(search) ||
      e.nom?.toLowerCase().includes(search) ||
      e.prenom?.toLowerCase().includes(search)
    );
  }

  getStatutBtnClass(actuel: any, btn: string) {
    const valeurStatut = (actuel && typeof actuel === 'object') ? (actuel.statut || actuel.name) : actuel;
    const sActuel = String(valeurStatut || '').trim().toUpperCase();
    const sBtn = String(btn || '').trim().toUpperCase();

    if (sActuel !== sBtn) return 'btn-light text-muted opacity-50';
    
    switch(sBtn) {
      case 'ACTIF': return 'btn-success text-white shadow-sm fw-bold';
      case 'INACTIF': return 'btn-danger text-white fw-bold';
      case 'SUSPENDU': return 'btn-warning text-dark fw-bold';
      default: return 'btn-secondary text-white';
    }
  }

  ouvrirConfirmationStatut(e: EmployeDto, st: string) {
    const valActuelle = (e.statut && typeof e.statut === 'object') ? (e.statut as any).statut : e.statut;
    if (String(valActuelle).toUpperCase() === String(st).toUpperCase()) return;
    this.confirmStatut = { show: true, employe: e, nouveauStatut: st };
  }

  confirmerChangementStatut() {
    const login = this.confirmStatut.employe.loginUtilisateur;
    const nouveau = this.confirmStatut.nouveauStatut;

    this.utilisateurService.modifierStatut(login, nouveau).subscribe({
      next: () => {
        this.confirmStatut.employe.statut = nouveau;
        this.showFeedback(`Statut de ${login} mis à jour : ${nouveau}`);
        this.confirmStatut.show = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur backend:', err);
        this.showFeedback('Erreur lors du changement de statut', true);
      }
    });
  }

  enregistrer() {
    if (this.employeForm.invalid) return;
    const val = this.employeForm.value;
    const roleId = this.typeActuel === TypeEmploye.CAISSIER ? 2 : 3;

    this.utilisateurService.enregistrerUtilisateur({ ...val, idRole: roleId }).pipe(
      switchMap(() => this.employeService.enregistrerEmploye({
          dateEmbauche: val.dateEmbauche,
          typeEmploye: this.typeActuel,
          commissionTaux: val.commissionTaux,
          loginUtilisateur: val.login,
          idAgence: 1
        } as any))
    ).subscribe({
      next: () => {
        this.showFeedback('Enregistré avec succès !');
        this.fermerModalAjout();
        this.chargerDonnees();
      },
      error: (err) => this.showFeedback('Erreur lors de la création', true)
    });
  }

  voirDetails(e: EmployeDto) { 
    this.employeSelectionne = e; 
    this.isModalOpen = true; 
  }
  fermerModal() { this.isModalOpen = false; }
  ouvrirModalAjout() { 
    this.employeForm.reset({
      dateEmbauche: new Date().toISOString().split('T')[0], 
      commissionTaux: 0, 
      statut: StatutGenerique.ACTIF
    }); 
    this.isAddModalOpen = true; 
  }
  fermerModalAjout() { this.isAddModalOpen = false; }
  showFeedback(m: string, err = false) { 
    this.feedbackModal = {show: true, message: m, isError: err}; 
    setTimeout(() => this.feedbackModal.show = false, 3000); 
  }
}