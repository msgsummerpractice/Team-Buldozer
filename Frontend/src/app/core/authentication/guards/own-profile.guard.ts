import { inject } from '@angular/core';
import { CanActivateFn, Router, UrlTree } from '@angular/router';
import { AuthenticationService } from '@core/authentication/services/authentication.service';
import { AuthorizationService } from '@core/authorization/services/authorization.service';
import { UserRoleEnum } from '@core/users/model/user-role';

export const ownProfileGuard: CanActivateFn = (route): boolean | UrlTree => {
  const auth = inject(AuthenticationService);
  const authorization = inject(AuthorizationService);
  const router = inject(Router);

  if (!auth.isAuthenticated()) {
    return router.createUrlTree(['/login']);
  }

  const currentUserId = authorization.getUserId();
  if (!currentUserId) {
    return router.createUrlTree(['/login']);
  }

  const paramId = route.paramMap.get('id');
  if (paramId === null) {
    return true;
  }

  const requestedId = Number(paramId);
  if (!Number.isInteger(requestedId) || requestedId <= 0) {
    return router.createUrlTree(['/info', String(currentUserId)]);
  }

  if (requestedId === currentUserId) {
    return true;
  }

  if (authorization.hasAnyRole([UserRoleEnum.ADMIN])) {
    return true;
  }

  return router.createUrlTree(['/info', String(currentUserId)]);
};
