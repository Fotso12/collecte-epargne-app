import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EmployeDto } from '../../donnees/modeles/employe.modele';

@Injectable({ providedIn: 'root' })
export class EmployeService {
  private apiUrl = 'http://localhost:8082/api/employes';

  constructor(private http: HttpClient) { }

  getCaissiers(): Observable<EmployeDto[]> {
    return this.http.get<EmployeDto[]>(`${this.apiUrl}/caissiers`);
  }

  getCollecteurs(): Observable<EmployeDto[]> {
    return this.http.get<EmployeDto[]>(`${this.apiUrl}/collecteurs`);
  }

  // Route synchronisée avec le contrôleur Java : /api/employes/collecteurs/{matricule}/clients
  getClientsByCollecteur(matricule: string): Observable<any[]> {
  // L'URL générée sera : http://localhost:8082/api/employes/collecteurs/COL202675898/clients
  return this.http.get<any[]>(`${this.apiUrl}/collecteurs/${matricule}/clients`);
}

  enregistrerEmploye(employe: EmployeDto): Observable<EmployeDto> {
    return this.http.post<EmployeDto>(this.apiUrl, employe);
  }

  modifierEmploye(matricule: string, employe: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${matricule}`, employe);
  }

  supprimerEmploye(matricule: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${matricule}`);
  }
}