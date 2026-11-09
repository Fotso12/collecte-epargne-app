import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TransactionDto } from '../../donnees/modeles/transaction.model';
import { AuthService } from './auth.service';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TransactionService {
  private readonly API_URL = 'http://localhost:8082/api/transactions';

  constructor(private http: HttpClient, private auth: AuthService) {}

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

  // Générer un reçu PDF pour une transaction (endpoint caissier)
  generateReceipt(id: string): Observable<any> {
    // Choisit l'endpoint selon le rôle courant (CAISSIER vs SUPERVISEUR)
    const user = (this as any).auth?.getUser ? (this as any).auth.getUser() : null;
    const roles = (this as any).auth?.getUserRoles ? (this as any).auth.getUserRoles() : [];

    // Debug logs pour aider au diagnostic (affichés dans la console du navigateur)
    console.debug('[TransactionService] generateReceipt called', { id, user, roles });

    // Si l'utilisateur est caissier, appeler le endpoint caissier
    if (roles && (roles.includes('CAISSIER') || roles.includes('ROLE_CAISSIER'))) {
      const idCaissier = user?.idEmploye ? String(user.idEmploye) : undefined;
      const url = `${environment.apiUrl}/api/caissier/receipts/${id}/generate`;
      const params: any = {};
      if (idCaissier) params.idCaissier = idCaissier;
      console.debug('[TransactionService] POST', url, { params });
      // Le backend renvoie actuellement du texte (String). Demander la réponse en texte brut
      return this.http.post(url, {}, { params, responseType: 'text' });
    }

    // Si superviseur ou admin, appeler le endpoint superviseur (batch/génération)
    if (roles && (roles.includes('SUPERVISEUR') || roles.includes('ADMIN') || roles.includes('ROLE_SUPERVISEUR') || roles.includes('ROLE_ADMIN'))) {
      const url = `${environment.apiUrl}/api/superviseur/receipts/generate`;
      // Backend superviseur endpoint attend un body (map). Fournir l'idTransaction pour la génération individuelle.
      console.debug('[TransactionService] POST', url, { body: { idTransaction: id } });
      // Le backend renvoie actuellement du texte (String). Demander la réponse en texte brut
      return this.http.post(url, { idTransaction: id }, { responseType: 'text' });
    }

    // Par défaut, tenter endpoint caissier (peut échouer si non autorisé)
    const fallbackUrl = `${environment.apiUrl}/api/caissier/receipts/${id}/generate`;
    console.debug('[TransactionService] POST fallback', fallbackUrl);
    return this.http.post(fallbackUrl, {}, { responseType: 'text' });
  }
}