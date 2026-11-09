import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject, tap } from 'rxjs';
import { Router } from '@angular/router';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  login: string;
  email: string;
  role: string;
  nom?: string;
  prenom?: string;
  idEmploye?: number;
  idAgence?: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly TOKEN_KEY = 'auth_token';
  private readonly USER_KEY = 'auth_user';

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(this.isBrowser() ? this.hasToken() : false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  public currentUserSubject = new BehaviorSubject<any>(this.isBrowser() ? this.getUser() : null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private router: Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  login(credentials: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>('http://localhost:8082/api/auth/login', credentials)
      .pipe(
        tap(response => {
          this.setToken(response.token);
          this.setUser(response);
          this.isAuthenticatedSubject.next(true);
          this.currentUserSubject.next(response);
        })
      );
  }

  /**
   * MÉTHODE AJOUTÉE : Change le mot de passe sur le backend
   * Utilise le login de l'utilisateur stocké
   */
  changePassword(newPassword: string): Observable<any> {
    const user = this.getUser();
    const login = user?.login;
    // Payload attendu par votre Map<String, String> côté Java
    const payload = { newPassword: newPassword };

    return this.http.put(`http://localhost:8082/api/utilisateurs/${login}/password`, payload, {
      responseType: 'text' // Car le backend renvoie un String brut
    });
  }

  updateLocalUserInfo(updatedUser: any): void {
    this.setUser(updatedUser);
    this.currentUserSubject.next(updatedUser);
  }

  logout(): void {
    if (this.isBrowser()) {
      localStorage.removeItem(this.TOKEN_KEY);
      localStorage.removeItem(this.USER_KEY);
    }
    this.isAuthenticatedSubject.next(false);
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }

  private setToken(token: string): void {
    if (this.isBrowser()) {
      localStorage.setItem(this.TOKEN_KEY, token);
    }
  }

  getToken(): string | null {
    if (this.isBrowser()) {
      return localStorage.getItem(this.TOKEN_KEY);
    }
    return null;
  }

  private setUser(user: any): void {
    if (this.isBrowser()) {
      localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    }
  }

  getUser(): any {
    if (this.isBrowser()) {
      const user = localStorage.getItem(this.USER_KEY);
      return user ? JSON.parse(user) : null;
    }
    return null;
  }

  private hasToken(): boolean {
    return !!this.getToken();
  }

  isLoggedIn(): boolean {
    return this.hasToken();
  }

  getUserRoles(): string[] {
    const user = this.getUser();
    if (!user) return [];
    const roles = user.roles || [user.role];
    // Normaliser les rôles en majuscules et sans espaces
    return roles.map((r: string) => String(r).toUpperCase().trim());
  }

  hasRole(role: string): boolean {
    if (!role) return false;
    const wanted = String(role).toUpperCase().trim();
    const roles = this.getUserRoles();
    // Supporter les formats avec/sans préfixe ROLE_
    if (roles.includes(wanted)) return true;
    if (roles.includes('ROLE_' + wanted)) return true;
    // Si roles contiennent des valeurs avec ROLE_ prefix, comparer sans préfixe
    const stripped = roles.map(r => r.replace(/^ROLE_/, ''));
    return stripped.includes(wanted);
  }
}