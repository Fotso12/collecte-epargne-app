import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SuperviseurService } from '../../../core/services/superviseur.service';
import { KpiService, SuperviseurKpi } from '../../../core/services/kpi.service';

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

  kpis: SuperviseurKpi | null = null;
  isLoading = true;
  isLoadingKpis = true;
  errorMessage = '';

  constructor(
    private superviseurService: SuperviseurService,
    private kpiService: KpiService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.chargerDashboard();
    this.chargerKpis();
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

  chargerKpis(): void {
    this.isLoadingKpis = true;
    this.kpiService.getKpis().subscribe({
      next: (data: SuperviseurKpi) => {
        this.kpis = data;
        this.isLoadingKpis = false;
        this.cdr.detectChanges();
      },
      error: (err: any) => {
        console.error('Erreur chargement KPIs:', err);
        this.isLoadingKpis = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Format montant
  formatMontant(montant: number): string {
    if (!montant) return '0 FCFA';
    return new Intl.NumberFormat('fr-FR', {
      style: 'decimal',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(montant) + ' FCFA';
  }

  // Obtenir la couleur du statut
  getStatusColor(status: string): string {
    return this.kpiService.getStatusColor(status);
  }

  // Obtenir le libellé du statut
  getStatusLabel(status: string): string {
    return this.kpiService.getStatusLabel(status);
  }

  // Rafraîchir les données
  rafraichir(): void {
    this.chargerDashboard();
    this.chargerKpis();
  }
}
