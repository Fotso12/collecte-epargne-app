import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class CompteService {
    private apiUrl = 'http://localhost:8082/api/comptes';

    constructor(private http: HttpClient) { }

    getComptesParClient(codeClient: string): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/client/${codeClient}`);
    }
}
