import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth';
import { DemandeService } from '../../core/services/demande';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html'
})
export class Dashboard implements OnInit {

  user: any = null;
  demandes: any[] = [];
  chargement = true;

  // Statistiques
  total = 0;
  brouillon = 0;
  enTraitement = 0;
  accepte = 0;
  refuse = 0;

  constructor(
    private authService: AuthService,
    private demandeService: DemandeService,
    private router: Router
  ) {}

  ngOnInit() {
    this.user = this.authService.getUser();
    this.chargerDemandes();
  }
  
  chargerDemandes() {
    this.demandeService.mesDemandes().subscribe({
      next: (data) => {
        console.log('Demandes reçues:', data);
        this.demandes = data;
        this.total = data.length;
        this.enTraitement = data.filter(d => d.statut === 'EN_TRAITEMENT').length;
        this.accepte = data.filter(d => d.statut === 'ACCEPTE').length;
        this.refuse = data.filter(d => d.statut === 'REFUSE').length;
        this.chargement = false;
      },
      error: (err) => {
        console.log('Erreur:', err); 
        this.chargement = false;
      }
    });
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
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
  
}