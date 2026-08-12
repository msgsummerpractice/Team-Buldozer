import { Injectable, inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthenticationService } from '../services/authentication.service';

@Injectable({
  providedIn: 'root',
})
export class RoleGuard {
  private auth = inject(AuthenticationService);
  private router = inject(Router);

  canActivate(allowedRoles: string[]): boolean {
    const userRole = this.auth.getUserRole();
    
    if (!userRole || !allowedRoles.includes(userRole)) {
      this.router.navigate(['/login']);
      return false;
    }
    
    return true;
  }
}

export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
  return () => {
    const auth = inject(AuthenticationService);
    const router = inject(Router);
    
    const userRole = auth.getUserRole();
    
    if (!userRole || !allowedRoles.includes(userRole)) {
      router.navigate(['/login']);
      return false;
    }
    
    return true;
  };
};
