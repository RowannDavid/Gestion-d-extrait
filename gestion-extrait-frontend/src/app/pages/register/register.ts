import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './register.html'
})
export class Register {

  nom = '';
  prenoms = '';
  email = '';
  password = '';
  erreur = '';
  succes = '';
  chargement = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onRegister() {
    this.erreur = '';
    this.succes = '';
    this.chargement = true;

    this.authService.register({
      nom: this.nom,
      prenoms: this.prenoms,
      email: this.email,
      password: this.password
    }).subscribe({
      next: () => {
        this.chargement = false;
        this.succes = 'Compte créé avec succès ! Redirection...';
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this.chargement = false;
        this.erreur = err.error?.message || 'Erreur lors de l\'inscription';
      }
    });
  }
}