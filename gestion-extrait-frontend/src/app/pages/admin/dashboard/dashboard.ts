import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { AdminService } from '../../../core/services/admin';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html'
})
export class Dashboard implements OnInit {

  demandes: any[] = [];
  chargement = true;
  total = 0;
  enTraitement = 0;
  accepte = 0;
  refuse = 0;

  constructor(
    private adminService: AdminService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.chargerDemandes();
  }

  chargerDemandes() {
    this.adminService.toutesLesDemandes().subscribe({
      next: (data) => {
        this.demandes = data;
        this.total = data.length;
        this.enTraitement = data.filter(d => d.statut === 'EN_TRAITEMENT').length;
        this.accepte = data.filter(d => d.statut === 'ACCEPTE').length;
        this.refuse = data.filter(d => d.statut === 'REFUSE').length;
        this.chargement = false;
      },
      error: () => this.chargement = false
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

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}