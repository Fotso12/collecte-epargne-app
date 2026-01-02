import { Component, OnInit } from '@angular/core';
import { ClientDto } from '../../../../donnees/modeles/client.modele';
import { ClientService } from '../../../../core/services/client.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common'; 

@Component({
  selector: 'app-liste-clients',
  standalone: true, 
  imports: [CommonModule, FormsModule], //pour activer *ngIf et *ngFor
  templateUrl: './liste-clients.html',
  styleUrls: ['./liste-clients.css']
})
export class ListeClientsComponent implements OnInit {
  clients: ClientDto[] = [];
  clientsFiltres: ClientDto[] = [];// Liste affichée après filtre
  chargement: boolean = true;

  // Variable pour stocker le client à afficher dans la modal
  clientSelectionne: ClientDto | null = null;

  // Variables de filtrage
  filtreCode: string = '';
  filtreCollecteur: string = '';
  filtreProfession: string = '';

  constructor(private clientService: ClientService) { }

  ngOnInit(): void {
    this.chargerClients();
  }

  // Pour l'ouverture et la fermeture du modal
isModalOpen: boolean = false;

voirDetails(client: ClientDto) {
  this.clientSelectionne = client;
  this.isModalOpen = true; // On ouvre manuellement
}

fermerModal() {
  this.isModalOpen = false;
}

  chargerClients() {
  this.clientService.getTousLesClients().subscribe({
    next: (donnees) => {
      this.clients = donnees;
      this.clientsFiltres = donnees; 
      this.chargement = false;
    },
    error: (err) => {
      console.error('Erreur lors du chargement', err);
      this.chargement = false;
    }
  });
}

 appliquerFiltres() {
  this.clientsFiltres = this.clients.filter(client => {
    const code = client.codeClient || ''; // Évite les erreurs si null
    const collecteur = client.nomCollecteur || '';
    const prof = client.profession || '';

    return code.toLowerCase().includes(this.filtreCode.toLowerCase()) &&
           collecteur.toLowerCase().includes(this.filtreCollecteur.toLowerCase()) &&
           prof.toLowerCase().includes(this.filtreProfession.toLowerCase());
  });
}

}