import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient) {}

  // Inscription
  register(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, data);
  }

  login(data: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, data).pipe(
      tap((response: any) => {
        console.log('Response login complète:', JSON.stringify(response));

        const user = response.user;

        // Normaliser le rôle (String ou Objet)
        if (user && typeof user.role === 'object') {
          user.role = user.role?.name || user.role?.toString() || 'USER';
        }

        localStorage.setItem('token', response.token);
        localStorage.setItem('user', JSON.stringify(user));

        console.log('User sauvegardé:', user);
        console.log('Role final:', user?.role);
      })
    );
  }

  isAdmin(): boolean {
    const user = this.getUser();
    console.log('User en storage:', user);      
    console.log('Role:', user?.role);           
    return user?.role === 'ADMIN';
  }

  // Déconnexion
  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  // Récupérer le token
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  // Récupérer l'utilisateur connecté
  getUser(): any {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  }

  // Vérifier si connecté
  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}