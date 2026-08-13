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
          if (response.userId) {
            localStorage.setItem('userId', String(response.userId));
          }
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
    localStorage.removeItem('userId');
    this.isAuthenticated.set(false);
  }

  getUserId(): number | null {
    const stored = localStorage.getItem('userId');
    if (stored) {
      const n = Number(stored);
      if (Number.isInteger(n) && n > 0) return n;
    }
    const token = localStorage.getItem('token');
    if (!token || !token.includes('.')) return null;
    try {
      const payload = JSON.parse(atob(token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/'))) as Record<string, unknown>;
      const candidate = payload['userId'] ?? payload['id'] ?? payload['uid'] ?? null;
      if (typeof candidate === 'number' && Number.isInteger(candidate) && candidate > 0) return candidate;
      if (typeof candidate === 'string') {
        const n = Number(candidate);
        if (Number.isInteger(n) && n > 0) return n;
      }
    } catch {
      return null;
    }
    return null;
  }
}
