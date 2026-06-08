import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { DemandeService } from '../../../core/services/demande';

@Component({
  selector: 'app-modifier',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './modifier.html'
})
export class Modifier implements OnInit {

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

  id: number = 0;
  chargement = false;
  chargementInit = true;
  erreur = '';
  succes = '';

  constructor(
    private demandeService: DemandeService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef  // ✅
  ) {}

  ngOnInit() {
    this.id = +this.route.snapshot.paramMap.get('id')!;
    this.chargerDemande();
  }

  chargerDemande() {
    this.demandeService.detail(this.id).subscribe({
      next: (data) => {
        console.log('Détail reçu:', data);
        this.demande = {
          nom:            data.nom || '',
          prenoms:        data.prenoms || '',
          genre:          data.genre || '',
          dateNaissance:  data.dateNaissance || '',
          villeNaissance: data.villeNaissance || '',
          telephone:      data.telephone || '',
          nomParent:      data.nomParent || '',
          typeExtrait:    data.typeExtrait || '',
          lieuLivraison:  data.lieuLivraison || ''
        };
        this.chargementInit = false;
        this.cdr.detectChanges(); // ✅
      },
      error: (err) => {
        console.log('Erreur:', err);
        this.erreur = 'Demande introuvable';
        this.chargementInit = false;
        this.cdr.detectChanges(); // ✅
      }
    });
  }

  onSubmit() {
    this.erreur = '';
    this.chargement = true;

    this.demandeService.modifier(this.id, this.demande).subscribe({
      next: () => {
        this.chargement = false;
        this.succes = 'Demande modifiée avec succès !';
        this.cdr.detectChanges();
        setTimeout(() => this.router.navigate(['/demandes', this.id]), 2000);
      },
      error: (err) => {
        this.chargement = false;
        this.erreur = err.error?.message || 'Erreur lors de la modification';
        this.cdr.detectChanges();
      }
    });
  }
}