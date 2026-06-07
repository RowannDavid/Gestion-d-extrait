import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth-guard';
import { adminGuard } from './core/guards/admin-guard';

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  // Pages publiques
  {
    path: 'login',
    loadComponent: () =>
      import('./pages/login/login').then(m => m.Login)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./pages/register/register').then(m => m.Register)
  },

  // Pages USER connecté
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./pages/dashboard/dashboard').then(m => m.Dashboard),
    canActivate: [authGuard]
  },
  {
    path: 'demandes',
    loadComponent: () =>
      import('./pages/demandes/liste/liste').then(m => m.Liste),
    canActivate: [authGuard]
  },
  {
    path: 'demandes/new',
    loadComponent: () =>
      import('./pages/demandes/nouvelle/nouvelle').then(m => m.Nouvelle),
    canActivate: [authGuard]
  },
  {
    path: 'demandes/:id',
    loadComponent: () =>
      import('./pages/demandes/detail/detail').then(m => m.Detail),
    canActivate: [authGuard]
  },

  // Pages ADMIN
  {
    path: 'admin',
    loadComponent: () =>
      import('./pages/admin/dashboard/dashboard').then(m => m.Dashboard),
    canActivate: [adminGuard]
  },
  {
    path: 'admin/demandes',
    loadComponent: () =>
      import('./pages/admin/demandes/demandes').then(m => m.Demandes),
    canActivate: [adminGuard]
  },

  // Redirection si route inconnue
  { path: '**', redirectTo: '/login' }
];