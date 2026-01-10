import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService } from '../../../../core/services/dashboard.service';

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
    pourcentageTransactionsValidees: 0
  };
  isLoading = true;

  constructor(private dashboardService: DashboardService) { }

  ngOnInit(): void {
    this.chargerStats();
  }

  chargerStats(): void {
    this.dashboardService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Erreur chargement stats', err);
        this.isLoading = false;
      }
    });
  }
}
