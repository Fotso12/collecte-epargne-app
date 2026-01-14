import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_CONFIG } from '../constants/api.constants';

@Injectable({
  providedIn: 'root'
})
export class PasswordService {

  private baseUrl = `${API_CONFIG.BASE_URL}/password`;

  constructor(private http: HttpClient) { }

  forgotPassword(email: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/forgot`, { email });
  }

  verifyCode(email: string, code: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/verify`, { email, code });
  }

  resetPassword(email: string, password: string): Observable<any> {
    // Les clés 'email' et 'password' doivent correspondre au DTO Java
    return this.http.post(`${this.baseUrl}/reset`, {
      email: email,
      password: password
    });
  }
}
