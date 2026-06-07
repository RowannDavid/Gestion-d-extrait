import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../core/services/admin';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-demandes',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './demandes.html'
})
export class Demandes implements OnInit {

  demandes: any[] = [];
  demandesFiltrees: any[] = [];
  chargement = true;
  filtreStatut = 'TOUS';
  chargementAction: number | null = null;
  chargementPdf: number | null = null;
  succes = '';
  erreur = '';

  constructor(
    private adminService: AdminService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef  // ✅
  ) {}

  ngOnInit() {
    this.chargerDemandes();
  }

  chargerDemandes() {
    this.chargement = true;
    this.adminService.toutesLesDemandes().subscribe({
      next: (data) => {
        console.log('Admin demandes:', data); // ✅ Debug
        this.demandes = [...data];
        this.filtrer();
        this.chargement = false;
        this.cdr.detectChanges(); // ✅
      },
      error: (err) => {
        console.log('Erreur admin:', err);
        this.chargement = false;
        this.cdr.detectChanges();
      }
    });
  }

  filtrer() {
    if (this.filtreStatut === 'TOUS') {
      this.demandesFiltrees = [...this.demandes];
    } else {
      this.demandesFiltrees = this.demandes.filter(
        d => d.statut === this.filtreStatut
      );
    }
    this.cdr.detectChanges(); // ✅
  }

  accepter(id: number) {
    this.chargementAction = id;
    this.adminService.accepter(id).subscribe({
      next: () => {
        this.succes = 'Demande acceptée !';
        this.chargementAction = null;
        this.chargerDemandes();
        setTimeout(() => this.succes = '', 3000);
      },
      error: () => {
        this.erreur = 'Erreur lors de l\'acceptation';
        this.chargementAction = null;
      }
    });
  }

  refuser(id: number) {
    this.chargementAction = id;
    this.adminService.refuser(id).subscribe({
      next: () => {
        this.succes = 'Demande refusée';
        this.chargementAction = null;
        this.chargerDemandes();
        setTimeout(() => this.succes = '', 3000);
      },
      error: () => {
        this.erreur = 'Erreur lors du refus';
        this.chargementAction = null;
      }
    });
  }

  genererPdf(id: number) {
    this.chargementPdf = id;
    this.adminService.genererPdf(id).subscribe({
      next: () => {
        this.succes = 'PDF généré avec succès !';
        this.chargementPdf = null;
        setTimeout(() => this.succes = '', 3000);
      },
      error: () => {
        this.erreur = 'Erreur génération PDF';
        this.chargementPdf = null;
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

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}