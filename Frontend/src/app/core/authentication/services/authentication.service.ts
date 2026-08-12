import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs';

export interface SignInResponse {
  token: string | null;
  roles: string[] | null;
  mfaRequired: boolean;
  message: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthenticationService {
  private http = inject(HttpClient);
  private readonly _isAuthenticated = signal<boolean>(!!localStorage.getItem('token'));
  public readonly isAuthenticated = this._isAuthenticated.asReadonly();

  login(username: string, password: string) {
    return this.http.post<SignInResponse>('http://localhost:8080/api/auth/login', {
      username,
      password,
    });
  }

  verifyMfa(username: string, token: string) {
    return this.http
      .post<SignInResponse>('http://localhost:8080/api/auth/mfa/verify', { username, token })
      .pipe(
        tap((response) => {
          if (response.token) {
            localStorage.setItem('token', response.token);
            this._isAuthenticated.set(true);
          }
        })
      );
  }

  register(user: any) {
    return this.http.post<any>('http://localhost:8080/api/register', user);
  }

  logout() {
    this._isAuthenticated.set(false);
    localStorage.removeItem('token');
  }
}
