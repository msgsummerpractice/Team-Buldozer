import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthorizationService } from '../services/authorization.service';

export const authorizationGuard = (allowedRoles: string[]): CanActivateFn => {
  return () => {
    const authz = inject(AuthorizationService);
    const router = inject(Router);

    const hasAccess = authz.hasAnyRole(allowedRoles);

    if (!hasAccess) {
      router.navigate(['/login']);
      return false;
    }

    return true;
  };
};
