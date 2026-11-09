import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UtilisateurDto } from '../../donnees/modeles/utilisateur.modele';
// Importez vos interfaces si elles existent
// import { UtilisateurDto } from '../../donnees/modeles/utilisateur.modele';

@Injectable({ providedIn: 'root' })
export class UtilisateurService {
  private apiUrl = 'http://localhost:8082/api/utilisateurs';

  constructor(private http: HttpClient) { }

  // Modification : Utiliser le typage explicite si possible
  enregistrerUtilisateur(data: any): Observable<any> {
    return this.http.post<any>(this.apiUrl, data);
  }

  modifierStatut(login: string, statut: string): Observable<any> {
    // Correspondance exacte avec le @RequestBody Map<String, String> du Controller Java
    return this.http.put<any>(`${this.apiUrl}/${login}/statut`, { statut: statut });
  }

  getUtilisateurParLogin(login: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${login}`);
  }

  supprimerUtilisateur(login: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${login}`);
  }

  getTousLesUtilisateurs(): Observable<UtilisateurDto[]> {
    return this.http.get<UtilisateurDto[]>(this.apiUrl);
  }
}