import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClientDto } from '../../donnees/modeles/client.modele';

@Injectable({
  providedIn: 'root'
})
export class ClientService {
  private apiUrl = 'http://localhost:8082/api/clients';

  constructor(private http: HttpClient) { }

  getTousLesClients(): Observable<ClientDto[]> {
    return this.http.get<ClientDto[]>(this.apiUrl);
  }

  // Nouvelle méthode pour l'importation CSV
  importerClientsCsv(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    // On précise responseType: 'text' car le backend renvoie souvent un message simple
    return this.http.post(`${this.apiUrl}/import`, formData, { responseType: 'text' });
  }

  enregistrerClient(client: ClientDto): Observable<ClientDto> {
    return this.http.post<ClientDto>(this.apiUrl, client);
  }

  getClientParNumero(numero: number): Observable<ClientDto> {
    return this.http.get<ClientDto>(`${this.apiUrl}/numero/${numero}`);
  }

  supprimerClient(code: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/code/${code}`);
  }

  modifierClientParNumero(numero: number, client: ClientDto): Observable<ClientDto> {
    return this.http.put<ClientDto>(`${this.apiUrl}/${numero}`, client);
  }

  modifierClientParCode(code: string, client: ClientDto): Observable<ClientDto> {
    return this.http.put<ClientDto>(`${this.apiUrl}/code/${code}`, client);
  }

}