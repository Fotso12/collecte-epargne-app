import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../../../../core/services/auth.service';

@Component({
  selector: 'app-validation-comptes',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './validation-comptes.html',
  styleUrls: ['./validation-comptes.css']
})
export class ValidationComptesComponent implements OnInit {
  demandes: any[] = [];
  loading = false;
  errorMessage: string | null = null;

  constructor(private http: HttpClient, private auth: AuthService) {}

  ngOnInit(): void {
    this.chargerDemandes();
  }

  chargerDemandes(): void {
    this.loading = true;
    this.errorMessage = null;
    // Récupérer seulement les demandes en attente
    this.http.get<any[]>('http://localhost:8082/api/demandes-ouverture/statut/EN_ATTENTE').subscribe({
      next: (data) => {
        this.demandes = data || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement demandes', err);
        this.errorMessage = err?.error?.message || err.message || 'Erreur serveur';
        this.loading = false;
      }
    });
  }

  valider(idDemande: number): void {
    const user = this.auth.getUser();
    const login = user?.login || '';
    if (!login) {
      alert('Impossible d\'identifier le superviseur connecté.');
      return;
    }
    this.http.post(`http://localhost:8082/api/demandes-ouverture/${idDemande}/valider`, { loginSuperviseur: login }).subscribe({
      next: () => this.chargerDemandes(),
      error: (err) => alert('Erreur validation: ' + (err?.error?.message || err.message))
    });
  }

  rejeter(idDemande: number): void {
    const motif = prompt('Motif du rejet :');
    if (motif === null) return; // annulation
    const user = this.auth.getUser();
    const login = user?.login || '';
    if (!login) {
      alert('Impossible d\'identifier le superviseur connecté.');
      return;
    }
    this.http.post(`http://localhost:8082/api/demandes-ouverture/${idDemande}/rejeter`, { loginSuperviseur: login, motifRejet: motif }).subscribe({
      next: () => this.chargerDemandes(),
      error: (err) => alert('Erreur rejet: ' + (err?.error?.message || err.message))
    });
  }
}
