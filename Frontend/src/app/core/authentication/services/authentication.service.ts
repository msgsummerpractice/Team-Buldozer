import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap, throwError } from 'rxjs';

export interface SignInResponse {
  token: string | null;
  roles: string[] | null;
  message: string;
}

@Injectable({ providedIn: 'root' })
export class AuthenticationService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api';

  readonly isAuthenticated = signal<boolean>(this.hasValidToken());
  readonly userRoles = signal<string[]>(this.loadRolesFromStorage());

  private hasValidToken(): boolean {
    try {
      return !!localStorage.getItem('token');
    } catch {
      return false;
    }
  }

  private loadRolesFromStorage(): string[] {
    try {
      const rolesJson = localStorage.getItem('roles');
      return rolesJson ? JSON.parse(rolesJson) : [];
    } catch {
      return [];
    }
  }

  getUserRole(): string | null {
    const roles = this.userRoles();
    return roles.length > 0 ? roles[0] : null;
  }

  login(email: string, password: string) {
    if (!email || !password) {
      return throwError(() => new Error('Email and password are required'));
    }
    return this.http
      .post<SignInResponse>(`${this.apiUrl}/authentication/login`, { email, password })
      .pipe(
        tap((response) => {
          if (response?.token && response?.roles) {
            this.storeAuthCredentials(response.token, response.roles);
          }
        })
      );
  }

  private storeAuthCredentials(token: string, roles: string[]): void {
    try {
      localStorage.setItem('token', token);
      localStorage.setItem('roles', JSON.stringify(roles));
      this.userRoles.set(roles);
      this.isAuthenticated.set(true);
    } catch {
      throw new Error('Failed to store authentication data');
    }
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('roles');
    this.isAuthenticated.set(false);
    this.userRoles.set([]);
  }
}
