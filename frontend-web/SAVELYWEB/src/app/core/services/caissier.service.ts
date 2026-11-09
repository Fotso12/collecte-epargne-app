import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class CaissierService {
  private apiUrl = `${environment.apiUrl}/api/caissier`;

  constructor(private http: HttpClient, private auth: AuthService) { }

  // Dashboard du caissier
  getDashboard(): Observable<any> {
    const user = this.auth.getUser();
    const idCaissier = user?.idEmploye;
    const query = idCaissier ? `?idCaissier=${idCaissier}` : '';
    return this.http.get(`${this.apiUrl}/dashboard${query}`);
  }

  // Transactions en attente de validation
  getTransactionsPending(): Observable<any[]> {
    const user = this.auth.getUser();
    const idCaissier = user?.idEmploye;
    const query = idCaissier ? `?idCaissier=${idCaissier}` : '';
    return this.http.get<any[]>(`${this.apiUrl}/transactions/pending${query}`);
  }

  // Valider une transaction
  validateTransaction(idTransaction: string, motif?: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/transactions/${idTransaction}/validate`, {
      confirmé: true,
      motif: motif || null
    });
  }

  // Rejeter une transaction
  rejectTransaction(idTransaction: string, motifRejet: string): Observable<any> {
    return this.http.post(`${this.apiUrl}/transactions/${idTransaction}/reject`, {
      motifRejet: motifRejet,
      confirmé: true
    });
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

  // Export
  exportReporting(format: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/reporting/export?format=${format}`, {
      responseType: 'blob'
    });
  }
}
