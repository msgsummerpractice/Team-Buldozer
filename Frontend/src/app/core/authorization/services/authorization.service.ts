import { Service, signal } from '@angular/core';
import { JwtInterface } from './jwt.interface';

@Service()
export class AuthorizationService {
  readonly userRoles = signal<string[]>([]);

  private decodeToken(token: string): JwtInterface | null {
    try {
      const payload = token.split('.')[1];
      const decoded = atob(payload);
      return JSON.parse(decoded);
    } catch {
      return null;
    }
  }

  loadRolesFromToken(): void {
    const token = localStorage.getItem('token');
    if (token) {
      const payload = this.decodeToken(token);
      if (payload?.roles) {
        this.userRoles.set(payload.roles);
      }
    }
  }

  hasAnyRole(roles: string[]): boolean {
    const userRoles = this.userRoles();
    return roles.some((role) => userRoles.includes(role));
  }

  clearRoles(): void {
    this.userRoles.set([]);
  }
}
