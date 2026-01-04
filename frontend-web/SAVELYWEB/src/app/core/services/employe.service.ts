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

  enregistrerEmploye(employe: EmployeDto): Observable<EmployeDto> {
    return this.http.post<EmployeDto>(this.apiUrl, employe);
  }

  /** Modification : On passe le matricule dans l'URL + l'objet complet */
  modifierEmploye(matricule: string, employe: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${matricule}`, employe);
  }

  supprimerEmploye(matricule: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${matricule}`);
  }
}