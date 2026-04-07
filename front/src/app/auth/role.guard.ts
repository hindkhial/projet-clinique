import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const roleGuard = (requiredRoles: string[]): CanActivateFn => () => {
  const auth   = inject(AuthService);
  const router = inject(Router);

  // not logged in → login page
  if (!auth.isLoggedIn()) {
    router.navigate(['/login']);
    return false;
  }

  // logged in but wrong role → forbidden page
  const hasAccess = requiredRoles.some(r => auth.hasRole(r));
  if (!hasAccess) {
    router.navigate(['/forbidden']);
    return false;
  }

  return true;
};