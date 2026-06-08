import { Component, OnInit, ChangeDetectorRef} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { DemandeService } from '../../../core/services/demande';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-liste',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './liste.html'
})
export class Liste implements OnInit {

  demandes: any[] = [];
  chargement = true;
  erreur = '';

  constructor(
    private demandeService: DemandeService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef  // ✅ Ajoute
  ) {}

  ngOnInit() {
    this.chargerDemandes();
  }

  chargerDemandes() {
    this.chargement = true;
    this.demandeService.mesDemandes().subscribe({
      next: (data) => {
        console.log('DATA:', data);
        this.demandes = [...data];
        this.chargement = false;
        this.cdr.detectChanges();
        console.log('chargement:', this.chargement);
        console.log('demandes:', this.demandes.length);
      },
      error: (err) => {
        console.log('ERREUR:', err);
        this.erreur = 'Erreur lors du chargement';
        this.chargement = false;
        this.cdr.detectChanges();
      }
    });
  }

  getBadgeClass(statut: string): string {
    switch(statut) {
      case 'BROUILLON':     return 'bg-secondary';
      case 'EN_TRAITEMENT': return 'bg-warning text-dark';
      case 'ACCEPTE':       return 'bg-success';
      case 'REFUSE':        return 'bg-danger';
      default:              return 'bg-secondary';
    }
  }

  getStatutIcon(statut: string): string {
    switch(statut) {
      case 'BROUILLON':     return 'bi-pencil';
      case 'EN_TRAITEMENT': return 'bi-hourglass-split';
      case 'ACCEPTE':       return 'bi-check-circle';
      case 'REFUSE':        return 'bi-x-circle';
      default:              return 'bi-circle';
    }
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}