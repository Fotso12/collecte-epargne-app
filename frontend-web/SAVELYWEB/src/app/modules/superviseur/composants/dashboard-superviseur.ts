import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SuperviseurService } from '../../../core/services/superviseur.service';

@Component({
  selector: 'app-superviseur-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-superviseur.html',
  styleUrl: './dashboard-superviseur.css'
})
export class SuperviseurDashboard implements OnInit {
  dashboard: any = {
    idSuperviseur: 0,
    nomSuperviseur: '',
    agenceNom: '',
    comptesEnAttenteApprobation: 0,
    collecteursTotal: 0,
    montantCollecteJour: 0,
    gainsJourSuperviseur: 0,
    meilleurCollecteur: null,
    historiquesCollection: []
  };

  isLoading = true;
  errorMessage = '';

  constructor(
    private superviseurService: SuperviseurService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.chargerDashboard();
  }

  chargerDashboard(): void {
    this.isLoading = true;
    this.superviseurService.getDashboard().subscribe({
      next: (data: any) => {
        this.dashboard = data;
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        this.errorMessage = 'Erreur lors du chargement du dashboard';
        console.error(err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Format montant
  formatMontant(montant: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'XAF'
    }).format(montant);
  }
}
