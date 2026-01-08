import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs'; // Ajout de forkJoin ici
import { map } from 'rxjs/operators';        // Ajout de map ici
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

  getClientsByCollecteur(matricule: string): Observable<any[]> {
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

  // La méthode getAll maintenant fonctionnelle
  getAll(): Observable<EmployeDto[]> {
    return forkJoin({
      caissiers: this.getCaissiers(),
      collecteurs: this.getCollecteurs()
    }).pipe(
      map((res: { caissiers: EmployeDto[], collecteurs: EmployeDto[] }) => [
        ...res.caissiers, 
        ...res.collecteurs
      ])
    );
  }
} 