import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AgenceZoneDto } from '../../donnees/modeles/gestion-agence-zone.modele';

@Injectable({ providedIn: 'root' })
export class AgenceZoneService {
  private apiUrl = 'http://localhost:8082/api/AgenceZone'; 

  constructor(private http: HttpClient) { }

  getAll(): Observable<AgenceZoneDto[]> {
    return this.http.get<AgenceZoneDto[]>(this.apiUrl);
  }

  save(agence: AgenceZoneDto): Observable<AgenceZoneDto> {
    return this.http.post<AgenceZoneDto>(this.apiUrl, agence);
  }

  update(id: number, agence: AgenceZoneDto): Observable<AgenceZoneDto> {
    return this.http.put<AgenceZoneDto>(`${this.apiUrl}/${id}`, agence);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}