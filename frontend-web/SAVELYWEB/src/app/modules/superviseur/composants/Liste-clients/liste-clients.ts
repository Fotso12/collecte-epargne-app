import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ClientDto, TypeCNI, StatutGenerique } from '../../../../donnees/modeles/client.modele';
import { ClientService } from '../../../../core/services/client.service';
import { UtilisateurService } from '../../../../core/services/utilisateur.service';
import { EmployeService } from '../../../../core/services/employe.service';
import { CompteService } from '../../../../core/services/compte.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { firstValueFrom, from, lastValueFrom, of } from 'rxjs';
import { catchError, mergeMap, tap, toArray } from 'rxjs/operators';

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
  comptesClient: any[] = []; // Ajout: Stockage des comptes
  chargementComptes: boolean = false; // Ajout: Loading state for accounts

  isModalOpen: boolean = false;
  isDeleteModalOpen: boolean = false;
  isSuccessModalOpen: boolean = false;
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

  // Variables pour le modal de résultat d'import
  isImportResultModalOpen: boolean = false;
  importSuccessCount: number = 0;
  importErrorCount: number = 0;

  constructor(
    private clientService: ClientService,
    private utilisateurService: UtilisateurService,
    private employeService: EmployeService,
    private compteService: CompteService,
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
        setTimeout(() => this.cdr.detectChanges(), 0);
      },
      error: (err: any) => {
        console.error('Erreur chargement', err);
        this.chargement = false;
        setTimeout(() => this.cdr.detectChanges(), 0);
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

  fermerModalImportResultat(): void {
    this.isImportResultModalOpen = false;
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

    // Appel direct au backend pour l'importation optimisée
    this.clientService.importerClientsCsv(file).subscribe({
      next: (response: any) => {
        this.importationEnCours = false;

        // Le backend renvoie maintenant un objet JSON avec les stats
        this.importSuccessCount = response.success || 0;
        this.importErrorCount = response.error || 0;
        this.isImportResultModalOpen = true;

        this.chargerClients();
        event.target.value = '';
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error("Erreur import backend", err);
        this.importationEnCours = false;
        alert("Erreur lors de l'importation : " + (err.error || err.message));
        this.cdr.detectChanges();
      }
    });
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

  // --- Logique Edition Client ---
  isEditModalOpen: boolean = false;
  clientToEdit: any = {};

  ouvrirModalEdition(client: ClientDto) {
    this.clientToEdit = { ...client }; // Copie pour éviter modif directe
    this.isEditModalOpen = true;
  }

  fermerModalEdition() {
    this.isEditModalOpen = false;
    this.clientToEdit = {};
  }

  confirmUpdateClient() {
    if (!this.clientToEdit.codeClient) return;

    this.clientService.modifierClientParCode(this.clientToEdit.codeClient, this.clientToEdit).subscribe({
      next: (updatedClient) => {
        const index = this.clients.findIndex(c => c.codeClient === updatedClient.codeClient);
        if (index !== -1) {
          this.clients[index] = updatedClient;
          this.appliquerFiltres();
        }
        this.fermerModalEdition();
        this.chargerClients();
      },
      error: (err) => {
        console.error("Erreur lors de la mise à jour client", err);
        alert("Erreur lors de la mise à jour du client.");
      }
    });
  }

  voirDetails(client: ClientDto): void {
    this.clientSelectionne = client;
    this.isModalOpen = true;
    this.comptesClient = [];
    this.chargementComptes = true;

    if (client.codeClient) {
      this.compteService.getComptesParClient(client.codeClient).subscribe({
        next: (data) => {
          this.comptesClient = data;
          this.chargementComptes = false;
          this.cdr.detectChanges();
        },
        error: (err) => {
          console.error("Erreur chargement comptes", err);
          this.chargementComptes = false;
          this.cdr.detectChanges();
        }
      });
    } else {
      this.chargementComptes = false;
    }
    this.cdr.detectChanges();
  }

  fermerModal(): void {
    this.isModalOpen = false;
    this.cdr.detectChanges();
  }

  telechargerModeleCSV(): void {
    const headers = 'numero_client,adresse,type_cni,numero_cni,date_naissance,lieu_naissance,profession,score_epargne,login_utilisateur,id_collecteur,nom,prenom,telephone,email,cniRectoPath,cniVersoPath,photoPath,ville';
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