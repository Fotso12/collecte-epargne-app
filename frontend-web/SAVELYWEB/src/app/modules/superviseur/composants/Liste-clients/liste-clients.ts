import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ClientDto, TypeCNI, StatutGenerique } from '../../../../donnees/modeles/client.modele';
import { ClientService } from '../../../../core/services/client.service';
import { UtilisateurService } from '../../../../core/services/utilisateur.service';
import { EmployeService } from '../../../../core/services/employe.service'; 
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { firstValueFrom } from 'rxjs';

@Component({
  selector: 'app-liste-clients',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './liste-clients.html',
  styleUrls: ['./liste-clients.css']
})
export class ListeClientsComponent implements OnInit {
  clients: ClientDto[] = [];
  clientsFiltres: ClientDto[] = [];
  chargement: boolean = true;
  importationEnCours: boolean = false;
  
  clientSelectionne: ClientDto | null = null;
  isModalOpen: boolean = false;
  isDeleteModalOpen: boolean = false;
  isSuccessModalOpen: boolean = false; // Nouveau : Modal de succès
  clientToDeleteCode: string | null = null;

  isAssignModalOpen: boolean = false;
  collecteurs: any[] = [];
  collecteurSelectionneId: string = ""; 
  clientPourAssignation: ClientDto | null = null;
  
  // Ajout pour la recherche dans le modal d'assignation
  rechercheCollecteur: string = '';

  filtreCode: string = '';
  filtreCollecteur: string = '';
  filtreProfession: string = '';

  selectedClientCodes: Set<string> = new Set();
  isBulkDelete: boolean = false;

  constructor(
    private clientService: ClientService,
    private utilisateurService: UtilisateurService,
    private employeService: EmployeService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.chargerClients();
  }

  // Getter pour filtrer la liste des collecteurs dans le select
  get collecteursFiltres() {
    if (!this.rechercheCollecteur) return this.collecteurs;
    return this.collecteurs.filter(c => 
      `${c.nom} ${c.prenom} ${c.matricule}`.toLowerCase().includes(this.rechercheCollecteur.toLowerCase())
    );
  }

  chargerClients(): void {
    this.chargement = true;
    this.selectedClientCodes.clear();
    this.clientService.getTousLesClients().subscribe({
      next: (donnees: ClientDto[]) => {
        this.clients = donnees;
        this.appliquerFiltres();
        this.chargement = false;
        this.cdr.detectChanges(); 
      },
      error: (err: any) => {
        console.error('Erreur chargement', err);
        this.chargement = false;
        this.cdr.detectChanges();
      }
    });
  }

  ouvrirModalAssignation(client: ClientDto): void {
    this.clientPourAssignation = client;
    this.collecteurSelectionneId = client.codeCollecteurAssigne ? client.codeCollecteurAssigne.toString() : "";
    this.rechercheCollecteur = ''; // Reset recherche
    this.isAssignModalOpen = true;

    if (this.collecteurs.length === 0) {
      this.employeService.getCollecteurs().subscribe({
        next: (data) => {
          this.collecteurs = data;
          this.cdr.detectChanges();
        }
      });
    }
    this.cdr.detectChanges();
  }

  fermerModalAssignation(): void {
    this.isAssignModalOpen = false;
    this.clientPourAssignation = null;
    this.collecteurSelectionneId = "";
    this.cdr.detectChanges();
  }

  fermerModalSucces(): void {
    this.isSuccessModalOpen = false;
    this.cdr.detectChanges();
  }

  async confirmerAssignation(): Promise<void> {
    if (!this.clientPourAssignation || !this.clientPourAssignation.codeClient) return;

    if (!this.collecteurSelectionneId || this.collecteurSelectionneId === "") {
      alert("Veuillez choisir un collecteur.");
      return;
    }

    const idNumerique = Number(this.collecteurSelectionneId);

    if (isNaN(idNumerique) || idNumerique === 0) {
      alert("Erreur : l'identifiant du collecteur sélectionné est invalide.");
      return;
    }

    try {
      const updateData: any = { 
        ...this.clientPourAssignation, 
        codeCollecteurAssigne: idNumerique 
      };

      await firstValueFrom(this.clientService.modifierClientParCode(this.clientPourAssignation.codeClient, updateData));
      
      this.fermerModalAssignation();
      this.chargerClients(); 
      
      this.isSuccessModalOpen = true; 
      this.cdr.detectChanges();

    } catch (err: any) {
      console.error('Erreur assignation', err);
      alert("Erreur Serveur : " + (err.error?.message || "Échec de l'assignation"));
    }
  }

  async onFichierSelectionne(event: any): Promise<void> {
    const file: File = event.target.files[0];
    if (!file) return;

    this.importationEnCours = true;
    const reader = new FileReader();

    reader.onload = async (e: any) => {
      const content = e.target.result as string;
      const lines = content.split('\n').filter(l => l.trim() !== '');
      const headers = lines[0].split(',').map(h => h.trim());

      let succesCount = 0;
      let erreurCount = 0;

      const importPromises = lines.slice(1).map(async (line, index) => {
        const data = line.split(',').map(d => d.trim());
        const row: any = {};
        headers.forEach((header, i) => row[header] = data[i]);
        if (!row.login_utilisateur) return;

        try {
          try {
            await firstValueFrom(this.utilisateurService.getUtilisateurParLogin(row.login_utilisateur));
          } catch (error) {
            await firstValueFrom(this.utilisateurService.enregistrerUtilisateur({
              login: row.login_utilisateur,
              nom: row.nom || 'Client',
              prenom: row.prenom || 'Nouveau',
              telephone: row.telephone || '00000000',
              email: row.email || `${row.login_utilisateur}@savely.com`,
              password: "Password123",
              idRole: 3,
              statut: StatutGenerique.ACTIF
            }));
          }
          const clientDto: any = {
            numeroClient: Number(row.numero_client),
            adresse: row.adresse,
            typeCni: row.type_cni,
            numCni: row.numero_cni,
            dateNaissance: row.date_naissance,
            lieuNaissance: row.lieu_naissance,
            profession: row.profession,
            scoreEpargne: Number(row.score_epargne || 0),
            loginUtilisateur: row.login_utilisateur,
            codeCollecteurAssigne: row.id_collecteur,
            statut: StatutGenerique.ACTIF
          };
          await firstValueFrom(this.clientService.enregistrerClient(clientDto));
          succesCount++;
        } catch (err) {
          erreurCount++;
          console.error(`Erreur ligne ${index + 2}:`, err);
        }
      });

      await Promise.all(importPromises);
      this.importationEnCours = false;
      alert(`Importation terminée !\nSuccès: ${succesCount}\nÉchecs: ${erreurCount}`);
      this.chargerClients();
      event.target.value = '';
    };
    reader.readAsText(file);
  }

  ouvrirModalSuppression(code: string | undefined): void {
    if (code) {
      this.clientToDeleteCode = code;
      this.isBulkDelete = false;
      this.isDeleteModalOpen = true;
      this.cdr.detectChanges();
    }
  }

  ouvrirModalSuppressionGroupee(): void {
    if (this.selectedClientCodes.size > 0) {
      this.isBulkDelete = true;
      this.isDeleteModalOpen = true;
      this.cdr.detectChanges();
    }
  }

  fermerModalSuppression(): void {
    this.isDeleteModalOpen = false;
    this.clientToDeleteCode = null;
    this.isBulkDelete = false;
    this.cdr.detectChanges();
  }

  async confirmerSuppression(): Promise<void> {
    try {
      if (this.isBulkDelete) {
        const codesASupprimer = Array.from(this.selectedClientCodes);
        const deletePromises = codesASupprimer.map(code => 
          firstValueFrom(this.clientService.supprimerClient(code))
        );
        await Promise.all(deletePromises);
      } else if (this.clientToDeleteCode) {
        await firstValueFrom(this.clientService.supprimerClient(this.clientToDeleteCode));
      }
      this.fermerModalSuppression();
      this.chargerClients();
    } catch (err) {
      alert('Une erreur est survenue lors de la suppression.');
    }
  }

  toggleAllSelections(event: any): void {
    const isChecked = event.target.checked;
    if (isChecked) {
      this.clientsFiltres.forEach(c => {
        if (c.codeClient) this.selectedClientCodes.add(c.codeClient);
      });
    } else {
      this.selectedClientCodes.clear();
    }
  }

  toggleClientSelection(code: string | undefined): void {
    if (!code) return;
    if (this.selectedClientCodes.has(code)) {
      this.selectedClientCodes.delete(code);
    } else {
      this.selectedClientCodes.add(code);
    }
  }

  isAllSelected(): boolean {
    return this.clientsFiltres.length > 0 && this.selectedClientCodes.size === this.clientsFiltres.length;
  }

  appliquerFiltres(): void {
    this.clientsFiltres = this.clients.filter(c => 
      (c.codeClient || '').toLowerCase().includes(this.filtreCode.toLowerCase()) &&
      (c.nomCollecteur || '').toLowerCase().includes(this.filtreCollecteur.toLowerCase()) &&
      (c.profession || '').toLowerCase().includes(this.filtreProfession.toLowerCase())
    );
  }

  voirDetails(client: ClientDto): void {
    this.clientSelectionne = client;
    this.isModalOpen = true;
    this.cdr.detectChanges();
  }

  fermerModal(): void {
    this.isModalOpen = false;
    this.cdr.detectChanges();
  }

  telechargerModeleCSV(): void {
    const headers = 'numero_client,adresse,type_cni,numero_cni,date_naissance,lieu_naissance,profession,score_epargne,login_utilisateur,id_collecteur,nom,prenom,telephone,email';
    const blob = new Blob([headers], { type: 'text/csv;charset=utf-8;' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.setAttribute('download', 'modele_clients.csv');
    link.click();
  }

  declencherChoixFichier(): void {
    const fileInput = document.getElementById('fileInput') as HTMLInputElement;
    fileInput?.click();
  }
}