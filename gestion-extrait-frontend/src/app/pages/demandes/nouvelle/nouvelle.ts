import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { DemandeService } from '../../../core/services/demande';

@Component({
  selector: 'app-nouvelle',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './nouvelle.html'
})
export class Nouvelle {

  demande = {
    nom: '',
    prenoms: '',
    genre: '',
    dateNaissance: '',
    villeNaissance: '',
    telephone: '',
    nomParent: '',
    typeExtrait: '',
    lieuLivraison: ''
  };

  chargement = false;
  erreur = '';
  succes = '';

  constructor(
    private demandeService: DemandeService,
    private router: Router
  ) {}

  onSubmit() {
    this.erreur = '';
    this.chargement = true;

    this.demandeService.creer(this.demande).subscribe({
      next: (data) => {
        this.chargement = false;
        this.succes = `Demande créée ! Référence : ${data.reference}`;
        setTimeout(() => this.router.navigate(['/demandes']), 2000);
      },
      error: (err) => {
        this.chargement = false;
        this.erreur = err.error?.message || 'Erreur lors de la création';
      }
    });
  }
}