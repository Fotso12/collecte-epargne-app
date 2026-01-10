import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RoleDto, TypeCompteDto, PlanCotisationDto, NotificationDto } from '../../donnees/modeles/parametres.modele';

@Injectable({
    providedIn: 'root'
})
export class ParametresService {
    private apiUrl = 'http://localhost:8082/api';

    constructor(private http: HttpClient) { }

    // --- ROLES ---
    getRoles(): Observable<RoleDto[]> {
        return this.http.get<RoleDto[]>(`${this.apiUrl}/roles`);
    }
    saveRole(dto: RoleDto): Observable<RoleDto> {
        return this.http.post<RoleDto>(`${this.apiUrl}/roles`, dto);
    }
    updateRole(id: number, dto: RoleDto): Observable<RoleDto> {
        return this.http.put<RoleDto>(`${this.apiUrl}/roles/${id}`, dto);
    }
    deleteRole(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/roles/${id}`);
    }

    // --- TYPES COMPTE ---
    getTypeComptes(): Observable<TypeCompteDto[]> {
        return this.http.get<TypeCompteDto[]>(`${this.apiUrl}/type-comptes`);
    }
    saveTypeCompte(dto: TypeCompteDto): Observable<TypeCompteDto> {
        return this.http.post<TypeCompteDto>(`${this.apiUrl}/type-comptes`, dto);
    }
    updateTypeCompte(id: number, dto: TypeCompteDto): Observable<TypeCompteDto> {
        return this.http.put<TypeCompteDto>(`${this.apiUrl}/type-comptes/${id}`, dto);
    }
    deleteTypeCompte(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/type-comptes/${id}`);
    }

    // --- PLANS COTISATION ---
    getPlans(): Observable<PlanCotisationDto[]> {
        return this.http.get<PlanCotisationDto[]>(`${this.apiUrl}/plan-cotisations`);
    }
    savePlan(dto: PlanCotisationDto): Observable<PlanCotisationDto> {
        return this.http.post<PlanCotisationDto>(`${this.apiUrl}/plan-cotisations`, dto);
    }
    updatePlan(id: string, dto: PlanCotisationDto): Observable<PlanCotisationDto> {
        return this.http.put<PlanCotisationDto>(`${this.apiUrl}/plan-cotisations/${id}`, dto);
    }
    deletePlan(id: string): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/plan-cotisations/${id}`);
    }

    // --- NOTIFICATIONS ---
    getNotifications(): Observable<NotificationDto[]> {
        return this.http.get<NotificationDto[]>(`${this.apiUrl}/notifications`);
    }
    sendNotification(dto: NotificationDto): Observable<NotificationDto> {
        return this.http.post<NotificationDto>(`${this.apiUrl}/notifications`, dto);
    }
}
