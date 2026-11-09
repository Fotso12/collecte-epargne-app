import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class ReportingService {
    private apiUrl = 'http://localhost:8082/api/reporting';

    constructor(private http: HttpClient) { }

    getTransactions(startDate: string, endDate: string): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/transactions?startDate=${startDate}&endDate=${endDate}`);
    }
}
