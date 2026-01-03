import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EmployeDto } from '../../donnees/modeles/employe.modele';

@Injectable({ providedIn: 'root' })
export class EmployeService {
  private apiUrl = 'http://localhost:8082/api/employes';

  constructor(private http: HttpClient) { }

  getTousLesEmployes(): Observable<EmployeDto[]> {
    return this.http.get<EmployeDto[]>(this.apiUrl);
  }

  getCaissiers(): Observable<EmployeDto[]> {
    return this.http.get<EmployeDto[]>(`${this.apiUrl}/caissiers`);
  }

  getCollecteurs(): Observable<EmployeDto[]> {
    return this.http.get<EmployeDto[]>(`${this.apiUrl}/collecteurs`);
  }

  getEmployeParMatricule(matricule: string): Observable<EmployeDto> {
    return this.http.get<EmployeDto>(`${this.apiUrl}/${matricule}`);
  }

  enregistrerEmploye(employe: EmployeDto): Observable<EmployeDto> {
    return this.http.post<EmployeDto>(this.apiUrl, employe);
  }
}