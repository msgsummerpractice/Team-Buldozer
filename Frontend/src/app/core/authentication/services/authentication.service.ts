import { Service, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs';
import type { SignInResponse } from './signin.response.interface';
import { environment } from '@environments/environment';
import { NotificationService } from '@core/notification/services/notification.service';

@Service()
export class AuthenticationService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;
  private readonly notification = inject(NotificationService);
  private readonly router = inject(Router);

  readonly isAuthenticated = signal<boolean>(this.hasValidToken());

  private hasValidToken(): boolean {
    return !!localStorage.getItem('token');
  }

  login(email: string, password: string) {
    return this.http.post<SignInResponse>(`${this.apiUrl}/auth/login`, { email, password }).pipe(
      tap((response) => {
        if (response.token) {
          localStorage.setItem('token', response.token);
          this.isAuthenticated.set(true);
          this.notification.showSuccess('success-messages.login-successful');
          this.router.navigate(['/home']);
        }
      })
    );
  }

  register(user: any) {
    return this.http.post<any>(`${this.apiUrl}/auth/register`, user);
  }

  logout(): void {
    localStorage.removeItem('token');
    this.isAuthenticated.set(false);
    localStorage.removeItem('userId');
  }

  private toNumericId(value: unknown): number | null {
    if (typeof value === 'number' && Number.isInteger(value) && value > 0) {
      return value;
    }

    if (typeof value === 'string') {
      const id = Number(value);
      if (Number.isInteger(id) && id > 0) {
        return id;
      }
    }

    return null;
  }

  private getUserIdFromToken(token: string): number | null {
    if (!token.includes('.')) return null;

    try {
      const payloadBase64 = token.split('.')[1];
      const payloadJson = atob(payloadBase64.replace(/-/g, '+').replace(/_/g, '/'));
      const payload = JSON.parse(payloadJson) as Record<string, unknown>;

      return (
        this.toNumericId(payload['userId']) ??
        this.toNumericId(payload['id']) ??
        this.toNumericId(payload['uid'])
      );
    } catch {
      return null;
    }
  }
}
