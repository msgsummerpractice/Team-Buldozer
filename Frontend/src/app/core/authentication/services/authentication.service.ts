import { Service, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';
import type { SignInResponse } from './signin.response.interface';
import { environment } from '../../../../environments/environment';

@Service()
export class AuthenticationService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  readonly isAuthenticated = signal<boolean>(this.hasValidToken());

  private hasValidToken(): boolean {
    return !!localStorage.getItem('token');
  }

  login(email: string, password: string) {
    return this.http
      .post<SignInResponse>(`${this.apiUrl}/authentication/login`, { email, password })
      .pipe(
        tap((response) => {
          if (response.token) {
            localStorage.setItem('token', response.token);
            this.isAuthenticated.set(true);
          }
        })
      );
  }

  register(user: any) {
    return this.http.post<any>(`${this.apiUrl}/register`, user);
  }

  logout(): void {
    localStorage.removeItem('token');
    this.isAuthenticated.set(false);
  }
}
