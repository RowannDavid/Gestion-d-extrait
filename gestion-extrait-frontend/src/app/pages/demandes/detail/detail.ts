import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute, Router } from '@angular/router';
import { DemandeService } from '../../../core/services/demande';

@Component({
  selector: 'app-detail',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './detail.html'
})
export class Detail implements OnInit {

  demande: any = null;
  chargement = true;
  chargementSoumettre = false;
  chargementTelecharger = false;
  erreur = '';
  succes = '';

  constructor(
    private demandeService: DemandeService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef  // ✅
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    console.log('ID demande:', id); // ✅ Debug
    if (id) {
      this.chargerDemande(+id);
    }
  }

  chargerDemande(id: number) {
    this.demandeService.detail(id).subscribe({
      next: (data) => {
        console.log('Détail reçu:', data); // ✅ Debug
        this.demande = data;
        this.chargement = false;
        this.cdr.detectChanges(); // ✅
      },
      error: (err) => {
        console.log('Erreur détail:', err);
        this.erreur = 'Demande introuvable';
        this.chargement = false;
        this.cdr.detectChanges();
      }
    });
  }

  soumettre() {
    this.chargementSoumettre = true;
    this.demandeService.soumettre(this.demande.id).subscribe({
      next: (data) => {
        this.demande.statut = data.statut;
        this.chargementSoumettre = false;
        this.succes = 'Demande soumise avec succès !';
        this.cdr.detectChanges();
      },
      error: () => {
        this.chargementSoumettre = false;
        this.erreur = 'Erreur lors de la soumission';
      }
    });
  }

  telecharger() {
    this.chargementTelecharger = true;
    this.demandeService.telecharger(this.demande.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `extrait_${this.demande.reference}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
        this.chargementTelecharger = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.chargementTelecharger = false;
        this.erreur = 'Extrait non disponible';
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
}