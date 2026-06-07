import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  private apiUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  // Toutes les demandes
  toutesLesDemandes(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/demandes`);
  }

  // Filtrer par statut
  parStatut(statut: string): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/demandes/statut/${statut}`);
  }

  // Détail
  detail(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/demandes/${id}`);
  }

  // Accepter
  accepter(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/demandes/${id}/accepter`, {});
  }

  // Refuser
  refuser(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/demandes/${id}/refuser`, {});
  }

  // Générer PDF
  genererPdf(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/demandes/${id}/generer-pdf`, {});
  }
}