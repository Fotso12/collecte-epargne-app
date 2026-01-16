import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SuperviseurService {
  private apiUrl = `${environment.apiUrl}/api/superviseur`;

  constructor(private http: HttpClient) { }

  // Dashboard du superviseur
  getDashboard(): Observable<any> {
    return this.http.get(`${this.apiUrl}/dashboard`);
  }

  // Comptes en attente d'approbation
  getComptesPending(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/comptes/pending`);
  }

  // Approuver un compte
  approveCompte(idCompte: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/comptes/${idCompte}/approve`, {
      confirmé: true
    });
  }

  // Rejeter un compte
  rejectCompte(idCompte: string, motifRejet: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/comptes/${idCompte}/reject`, {
      motifRejet: motifRejet,
      confirmé: true
    });
  }

  // Meilleur collecteur
  getBestCollector(): Observable<any> {
    return this.http.get(`${this.apiUrl}/kpi/best-collector`);
  }

  // Historique collection
  getCollectionHistory(period: string = 'DAILY'): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/kpi/collection-history?period=${period}`);
  }

  // Lister les clients de l'agence
  getClients(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/clients`);
  }

  // Lister les collecteurs de l'agence
  getCollecteurs(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/collecteurs`);
  }

  // Reporting financier
  getReporting(): Observable<any> {
    return this.http.get(`${this.apiUrl}/reporting/financial`);
  }

  // Générer reçus
  generateReceipts(): Observable<any> {
    return this.http.post(`${this.apiUrl}/receipts/generate`, {});
  }
}
