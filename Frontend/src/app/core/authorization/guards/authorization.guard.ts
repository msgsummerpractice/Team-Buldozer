import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthorizationService } from '../services/authorization.service';

export const authorizationGuard = (allowedRoles: string[]): CanActivateFn => {
  return () => {
    const authorization = inject(AuthorizationService);
    const router = inject(Router);

    const hasAccess = authorization.hasAnyRole(allowedRoles);

    if (!hasAccess) {
      router.navigate(['/home']);
      return false;
    }

    return true;
  };
};
