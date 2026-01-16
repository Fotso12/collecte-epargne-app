import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CaissierService } from '../../../../core/services/caissier.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-caissier-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class CaissierDashboard implements OnInit {
  dashboard: any = {
    idCaissier: 0,
    nomCaissier: '',
    agenceNom: '',
    transactionsEnAttente: 0,
    transactionsValideeAujourdhui: 0,
    montantValideAujourd: 0,
    clientsTotal: 0,
    collecteursTotal: 0,
    gainsJourCaissier: 0,
    montantValideTotal: 0,
    transactionsTotal: 0,
    topCollecteurs: [],
    derniereTransactions: []
  };

  isLoading = true;
  errorMessage = '';

  constructor(
    private caissierService: CaissierService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.chargerDashboard();
  }

  chargerDashboard(): void {
    this.isLoading = true;
    this.caissierService.getDashboard().subscribe({
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

  // Format montant en devise
  formatMontant(montant: number): string {
    return new Intl.NumberFormat('fr-FR', {
      style: 'currency',
      currency: 'XAF'
    }).format(montant);
  }

  // Formatage nombre
  formatNumber(num: number): string {
    return num.toString().padStart(2, '0');
  }
}
