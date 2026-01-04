import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ClientDto } from '../../../../donnees/modeles/client.modele';
import { ClientService } from '../../../../core/services/client.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-liste-clients',
  standalone: true,
  imports: [CommonModule, FormsModule], // active *ngIf, *ngFor et ngModel
  templateUrl: './liste-clients.html',
  styleUrls: ['./liste-clients.css']
})
export class ListeClientsComponent implements OnInit {
  clients: ClientDto[] = [];
  clientsFiltres: ClientDto[] = []; // Liste affichée après filtre
  chargement: boolean = true;

  // Variable pour stocker le client à afficher dans la modal
  clientSelectionne: ClientDto | null = null;

  // Variables de filtrage
  filtreCode: string = '';
  filtreCollecteur: string = '';
  filtreProfession: string = '';

  // Pour l'ouverture et la fermeture du modal
  isModalOpen: boolean = false;

  constructor(
    private clientService: ClientService,
    private cdr: ChangeDetectorRef // Ajouté pour forcer le rafraîchissement
  ) { }

  ngOnInit(): void {
    this.chargerClients();
  }

  voirDetails(client: ClientDto) {
    this.clientSelectionne = client;
    this.isModalOpen = true; 
    this.cdr.detectChanges(); // Force la mise à jour pour l'ouverture
  }

  fermerModal() {
    this.isModalOpen = false;
    this.cdr.detectChanges(); // Force la mise à jour pour la fermeture
  }

  chargerClients() {
    this.chargement = true;
    this.clientService.getTousLesClients().subscribe({
      next: (donnees) => {
        this.clients = donnees;
        this.clientsFiltres = [...donnees]; // Copie pour l'affichage initial
        this.chargement = false;
        
        // --- C'est ici que la magie opère ---
        // On force Angular à redessiner le tableau car le retour 
        // de l'API peut arriver hors cycle de détection.
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        console.error('Erreur lors du chargement', err);
        this.chargement = false;
        this.cdr.detectChanges();
      }
    });
  }

  appliquerFiltres() {
    this.clientsFiltres = this.clients.filter(client => {
      const code = client.codeClient || ''; 
      const collecteur = client.nomCollecteur || '';
      const prof = client.profession || '';

      return code.toLowerCase().includes(this.filtreCode.toLowerCase()) &&
             collecteur.toLowerCase().includes(this.filtreCollecteur.toLowerCase()) &&
             prof.toLowerCase().includes(this.filtreProfession.toLowerCase());
    });
    // Pas besoin de detectChanges ici car l'événement (input) du HTML le fait déjà
  }
}