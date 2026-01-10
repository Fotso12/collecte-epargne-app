import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../../../core/services/dashboard.service';
import { AgenceZoneService } from '../../../../core/services/gestion-agence-zone.service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css'
})
export class Dashboard implements OnInit {
  stats: any = {
    totalClients: 0,
    totalCollecteurs: 0,
    totalCaissiers: 0,
    volumeCotisation: 0,
    totalAgences: 0,
    pourcentageTransactionsValidees: 0
  };
  isLoading = true;

  loader = true;

  constructor(
    private dashboardService: DashboardService,
    private agenceService: AgenceZoneService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit(): void {
    this.chargerStats();
  }

  chargerStats(): void {
    const subs = forkJoin({
      stats: this.dashboardService.getStats(),
      agences: this.agenceService.getAll()
    }).subscribe({
      next: (res) => {
        this.stats = res.stats;
        this.stats.totalAgences = res.agences.length; // Add manual count
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur chargement stats', err);
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }
}
