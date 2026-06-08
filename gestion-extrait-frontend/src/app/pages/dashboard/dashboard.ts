import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { AuthService } from '../../core/services/auth';
import { DemandeService } from '../../core/services/demande';

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

  total = 0;
  brouillon = 0;
  enTraitement = 0;
  accepte = 0;
  refuse = 0;

  constructor(
    private authService: AuthService,
    private demandeService: DemandeService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.user = this.authService.getUser();
    this.chargerDemandes();
  }

  chargerDemandes() {
    this.chargement = true;
    this.demandeService.mesDemandes().subscribe({
      next: (data) => {
        console.log('Demandes dashboard:', data);
        this.demandes = [...data];
        this.total        = data.length;
        this.brouillon    = data.filter(d => d.statut === 'BROUILLON').length;
        this.enTraitement = data.filter(d => d.statut === 'EN_TRAITEMENT').length;
        this.accepte      = data.filter(d => d.statut === 'ACCEPTE').length;
        this.refuse       = data.filter(d => d.statut === 'REFUSE').length;
        this.chargement   = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.log('Erreur dashboard:', err);
        this.chargement = false;
        this.cdr.detectChanges();
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

  getStatutIcon(statut: string): string {
    switch(statut) {
      case 'BROUILLON':     return 'bi-pencil';
      case 'EN_TRAITEMENT': return 'bi-hourglass-split';
      case 'ACCEPTE':       return 'bi-check-circle';
      case 'REFUSE':        return 'bi-x-circle';
      default:              return 'bi-circle';
    }
  }
}