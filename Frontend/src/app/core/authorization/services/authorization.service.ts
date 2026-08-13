import { Service } from '@angular/core';
import { jwtDecode } from 'jwt-decode';
import { JwtInterface } from './jwt.interface';

@Service()
export class AuthorizationService {
  hasAnyRole(roles: string[]): boolean {
    const token = localStorage.getItem('token');
    if (token) {
      const decodedToken = jwtDecode<JwtInterface>(token);
      const userRoles = (decodedToken?.roles || []).map((r) =>
        r.startsWith('ROLE_') ? r.substring(5) : r,
      );
      return roles.some((role) => userRoles.includes(role));
    }
    return false;
  }
}
