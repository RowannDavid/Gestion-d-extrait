import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth';

export const adminGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const user = authService.getUser();
  console.log('Guard - User:', user);           // ✅ Debug
  console.log('Guard - isAdmin:', authService.isAdmin()); // ✅ Debug

  if (authService.isLoggedIn() && authService.isAdmin()) {
    return true;
  }

  router.navigate(['/dashboard']);
  return false;
};