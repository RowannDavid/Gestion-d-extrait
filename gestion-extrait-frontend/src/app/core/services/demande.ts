import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class DemandeService {

  private apiUrl = 'http://localhost:8080/api/demandes';

  constructor(private http: HttpClient) {}

  // Créer une demande
  creer(data: any): Observable<any> {
    return this.http.post(this.apiUrl, data);
  }

  // Mes demandes
  mesDemandes(): Observable<any[]> {
  return this.http.get<any>(`${this.apiUrl}`).pipe(
    map((response: any) => {
      console.log('Response demandes:', response); // ✅ Debug
      // Si c'est déjà un tableau
      if (Array.isArray(response)) return response;
      // Si c'est un objet avec une propriété
      if (response.demandes) return response.demandes;
      if (response.data) return response.data;
      return [];
    })
  );
}
  // Détail
  detail(id: number): Observable<any> {
    return this.http.get(`${this.apiUrl}/${id}`);
  }

  // Modifier
  modifier(id: number, data: any): Observable<any> {
    return this.http.put(`${this.apiUrl}/${id}`, data);
  }

  // Soumettre
  soumettre(id: number): Observable<any> {
    return this.http.post(`${this.apiUrl}/${id}/soumettre`, {});
  }

  // Télécharger extrait
  telecharger(id: number): Observable<Blob> {
    return this.http.get(`${this.apiUrl}/${id}/telecharger`, {
      responseType: 'blob'
    });
  }
}