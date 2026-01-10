// 1. Ajoute ChangeDetectorRef dans l'importation existante
import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { forkJoin, of } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { AgenceZoneDto } from '../../../../donnees/modeles/gestion-agence-zone.modele';
import { EmployeDto } from '../../../../donnees/modeles/employe.modele';
import { ClientDto } from '../../../../donnees/modeles/client.modele';
import { UtilisateurDto } from '../../../../donnees/modeles/utilisateur.modele';
import { AgenceZoneService } from '../../../../core/services/gestion-agence-zone.service';
import { ClientService } from '../../../../core/services/client.service';
import { EmployeService } from '../../../../core/services/employe.service';
import { UtilisateurService } from '../../../../core/services/utilisateur.service';
import { StatutGenerique } from '../../../../donnees/modeles/enums';

@Component({
  selector: 'app-gestion-agence-zone',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gestion-agence-zone.html',
  styleUrls: ['./gestion-agence-zone.css']
})
export class GestionAgenceZoneComponent implements OnInit {
  agences: AgenceZoneDto[] = [];
  agencesFiltrees: AgenceZoneDto[] = [];
  rechercheTerme: string = '';
  chargement = false;
  isFormModalOpen = false;
  isEntitiesModalOpen = false;

  agenceForm: AgenceZoneDto = this.initialiserForm();
  agenceSelectionnee?: AgenceZoneDto;
  tabActif: 'employes' | 'clients' | 'utilisateurs' = 'employes';

  listeEmployes: EmployeDto[] = [];
  listeClients: ClientDto[] = [];
  listeUtilisateurs: UtilisateurDto[] = [];

  // 2. Utilise le type directement sans le "import(...)"
  constructor(
    private agenceService: AgenceZoneService,
    private clientService: ClientService,
    private employeService: EmployeService,
    private utilisateurService: UtilisateurService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.chargerAgences();
  }

  initialiserForm(): AgenceZoneDto {
    return {
      code: '', nom: '', ville: '', quartier: '',
      adresse: '', telephone: '', description: '', position: '',
      statut: StatutGenerique.ACTIF
    };
  }

  chargerAgences() {
    this.chargement = true;
    this.agenceService.getAll().subscribe({
      next: (data) => {
        this.agences = data;
        this.agencesFiltrees = data;
        this.chargement = false;
        this.cdr.detectChanges(); // Maintenant ceci fonctionnera
      },
      error: (err) => {
        console.error("Erreur chargement agences", err);
        this.chargement = false;
        this.cdr.detectChanges();
      }
    });
  }

  filtrerAgences() {
    const term = this.rechercheTerme.toLowerCase();
    this.agencesFiltrees = this.agences.filter(a =>
      a.nom.toLowerCase().includes(term) ||
      a.code.toLowerCase().includes(term) ||
      (a.ville && a.ville.toLowerCase().includes(term))
    );
  }

  ouvrirCreation() {
    this.agenceForm = this.initialiserForm();
    this.isFormModalOpen = true;
  }

  ouvrirEdition(agence: AgenceZoneDto) {
    this.agenceForm = { ...agence };
    this.isFormModalOpen = true;
  }

  enregistrer() {
    const payload = { ...this.agenceForm };
    if (!payload.idAgence) {
      (payload as any).dateCreation = new Date().toISOString();
    }

    const action = payload.idAgence
      ? this.agenceService.update(payload.idAgence, payload)
      : this.agenceService.save(payload);

    action.subscribe({
      next: () => {
        this.chargerAgences();
        this.isFormModalOpen = false; // Close form first
        this.cdr.detectChanges();
        this.showSuccess(payload.idAgence ? "L'agence a été modifiée avec succès !" : "L'agence a été créée avec succès !");
      },
      error: (err) => {
        console.error(err);
        this.showError("Une erreur est survenue lors de l'enregistrement. Code: " + err.status);
      }
    });
  }

  voirEntites(agence: AgenceZoneDto) {
    this.agenceSelectionnee = agence;
    this.isEntitiesModalOpen = true;
    this.chargement = true;

    forkJoin({
      emps: this.employeService.getAll().pipe(catchError(() => of([]))),
      clis: this.clientService.getTousLesClients().pipe(catchError(() => of([]))),
      utils: this.utilisateurService.getTousLesUtilisateurs().pipe(catchError(() => of([])))
    }).subscribe({
      next: (res) => {
        const targetId = String(agence.idAgence);

        this.listeEmployes = res.emps.filter(e => String(e.idAgence) === targetId);
        this.listeClients = res.clis.filter((c: any) => String(c.idAgence) === targetId);

        const loginsDeLAgence = this.listeEmployes.map(e => e.loginUtilisateur);
        this.listeUtilisateurs = res.utils.filter((u: UtilisateurDto) => loginsDeLAgence.includes(u.login));

        this.chargement = false;
        this.cdr.detectChanges(); // Ajouté ici aussi pour rafraîchir la vue après le forkJoin
      },
      error: () => {
        this.chargement = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Modal states
  isSuccessModalOpen = false;
  successMessage = '';
  isErrorModalOpen = false;
  errorMessage = '';

  fermerModals() {
    this.isFormModalOpen = false;
    this.isEntitiesModalOpen = false;
    this.isSuccessModalOpen = false;
    this.isErrorModalOpen = false;
    this.cdr.detectChanges();
  }

  showSuccess(message: string) {
    this.successMessage = message;
    this.isSuccessModalOpen = true;
    this.cdr.detectChanges();
  }

  showError(message: string) {
    this.errorMessage = message;
    this.isErrorModalOpen = true;
    this.cdr.detectChanges();
  }
}