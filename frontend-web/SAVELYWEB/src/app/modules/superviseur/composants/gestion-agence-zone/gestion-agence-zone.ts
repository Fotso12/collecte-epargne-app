import { Component, OnInit } from '@angular/core';
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

  constructor(
    private agenceService: AgenceZoneService,
    private clientService: ClientService,
    private employeService: EmployeService,
    private utilisateurService: UtilisateurService
  ) {}

  ngOnInit(): void {
    this.chargerAgences();
  }

  initialiserForm(): AgenceZoneDto {
    return { 
      code: '', nom: '', ville: '', quartier: '', 
      adresse: '', telephone: '', description: '', 
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
      },
      error: (err) => {
        console.error("Erreur chargement agences", err);
        this.chargement = false;
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
        this.fermerModals();
        alert(payload.idAgence ? "Modification réussie !" : "Création réussie !");
      },
      error: (err) => alert("Erreur d'enregistrement : " + err.status)
    });
  }

  voirEntites(agence: AgenceZoneDto) {
    this.agenceSelectionnee = agence;
    this.isEntitiesModalOpen = true;
    this.chargement = true;

    // Utilisation de catchError pour éviter que tout échoue si une liste est vide
    forkJoin({
      emps: this.employeService.getAll().pipe(catchError(() => of([]))),
      clis: this.clientService.getTousLesClients().pipe(catchError(() => of([]))),
      utils: this.utilisateurService.getTousLesUtilisateurs().pipe(catchError(() => of([])))
    }).subscribe({
      next: (res) => {
        // Filtrage strict par ID agence
        const targetId = String(agence.idAgence);
        
        this.listeEmployes = res.emps.filter(e => String(e.idAgence) === targetId);
        this.listeClients = res.clis.filter((c: any) => String(c.idAgence) === targetId);

        // Utilisateurs : on cherche ceux dont le login correspond aux employés filtrés
        const loginsDeLAgence = this.listeEmployes.map(e => e.loginUtilisateur);
        this.listeUtilisateurs = res.utils.filter((u: UtilisateurDto) => loginsDeLAgence.includes(u.login));
        
        this.chargement = false;
      },
      error: () => this.chargement = false
    });
  }

  fermerModals() {
    this.isFormModalOpen = false;
    this.isEntitiesModalOpen = false;
  }
}