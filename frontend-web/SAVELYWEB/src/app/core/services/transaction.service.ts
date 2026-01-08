import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TransactionDto } from '../../donnees/modeles/transaction.model';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private readonly API_URL = 'http://localhost:8082/api/transactions';

  constructor(private http: HttpClient) {}

  getAll(): Observable<TransactionDto[]> {
    return this.http.get<TransactionDto[]>(this.API_URL);
  }

  validerParCaissier(id: string, idCaissier: string): Observable<TransactionDto> {
    return this.http.put<TransactionDto>(`${this.API_URL}/${id}/valider-caissier/${idCaissier}`, {});
  }

  validerParSuperviseur(id: string, idSuperviseur: string): Observable<TransactionDto> {
    return this.http.put<TransactionDto>(`${this.API_URL}/${id}/valider-superviseur/${idSuperviseur}`, {});
  }

  rejeter(id: string, motif: string): Observable<void> {
    // Utilisation de params pour correspondre au @RequestParam de Spring
    return this.http.put<void>(`${this.API_URL}/${id}/rejeter`, {}, {
      params: { motif: motif }
    });
  }
}