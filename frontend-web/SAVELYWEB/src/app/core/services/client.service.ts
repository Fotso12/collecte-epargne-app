import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClientDto } from '../../donnees/modeles/client.modele';

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  // L'URL de ton API Spring Boot
  private apiUrl = 'http://localhost:8082/api/clients';

  constructor(private http: HttpClient) { }

  // Récupérer tous les clients (pour le Superviseur)
  getTousLesClients(): Observable<ClientDto[]> {
    return this.http.get<ClientDto[]>(this.apiUrl);
  }

  // Créer un nouveau client (pour le Caissier ou Superviseur)
  enregistrerClient(client: ClientDto): Observable<ClientDto> {
    return this.http.post<ClientDto>(this.apiUrl, client);
  }

  // Récupérer un client par son numéro
  getClientParNumero(numero: number): Observable<ClientDto> {
    return this.http.get<ClientDto>(`${this.apiUrl}/numero/${numero}`);
  }

  // Supprimer un client
  supprimerClient(code: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/code/${code}`);
  }

  // Mettre à jour par numéro (PUT /api/clients/{numeroClient})
  modifierClientParNumero(numero: number, client: ClientDto): Observable<ClientDto> {
    return this.http.put<ClientDto>(`${this.apiUrl}/${numero}`, client);
  }

  // Mettre à jour par code (PUT /api/clients/code/{codeClient})
  modifierClientParCode(code: string, client: ClientDto): Observable<ClientDto> {
    return this.http.put<ClientDto>(`${this.apiUrl}/code/${code}`, client);
  }
}