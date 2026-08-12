import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';

export interface SignInResponse {
  token: string | null;
  roles: string[] | null;
  message: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private http = inject(HttpClient);
  isAuthenticated = signal<boolean>(!!localStorage.getItem('token'));
  userRoles = signal<string[]>(this.loadRolesFromStorage());

  private loadRolesFromStorage(): string[] {
    const rolesJson = localStorage.getItem('roles');
    return rolesJson ? JSON.parse(rolesJson) : [];
  }

  getUserRole(): string | null {
    const roles = this.userRoles();
    return roles.length > 0 ? roles[0] : null;
  }

  login(username: string, password: string) {
    return this.http.post<SignInResponse>('http://localhost:8080/api/auth/login', {
      username,
      password,
    }).pipe(
      tap((response) => {
        if (response.token && response.roles) {
          localStorage.setItem('token', response.token);
          localStorage.setItem('roles', JSON.stringify(response.roles));
          this.userRoles.set(response.roles);
          this.isAuthenticated.set(true);
        }
      })
    );
  }

  register(user: any) {
    return this.http.post<any>('http://localhost:8080/api/auth/register', user);
  }

  logout() {
    this.isAuthenticated.set(false);
    this.userRoles.set([]);
    localStorage.removeItem('token');
    localStorage.removeItem('roles');
  }
}
