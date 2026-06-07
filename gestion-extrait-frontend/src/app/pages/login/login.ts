import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './login.html'
})
export class Login {

  email = '';
  password = '';
  erreur = '';
  chargement = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onLogin() {
    this.erreur = '';
    this.chargement = true;

    this.authService.login({ email: this.email, password: this.password }).subscribe({
      next: (response) => {
        this.chargement = false;
        // Rediriger selon le rôle
        if (response.user.role === 'ADMIN') {
          this.router.navigate(['/admin']);
        } else {
          this.router.navigate(['/dashboard']);
        }
      },
      error: (err) => {
        this.chargement = false;
        this.erreur = 'Email ou mot de passe incorrect';
      }
    });
  }
}