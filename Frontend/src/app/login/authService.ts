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
export class Authentication {
  private http = inject(HttpClient);
  isAuthenticated = signal<boolean>(!!localStorage.getItem('token'));

  login(username: string, password: string) {
    return this.http.post<SignInResponse>(
      'http://localhost:8080/api/auth/login',
      {
        username,
        password,
      },
    );
  }

  verifyMfa(username: string, token: string) {
    return this.http
      .post<SignInResponse>(
        'http://localhost:8080/api/auth/mfa/verify',
        { username, token },
      )
      .pipe(
        tap((response) => {
          if (response.token) {
            localStorage.setItem('token', response.token);
            this.isAuthenticated.set(true);
          }
        }),
      );
  }

  register(user: any) {
    return this.http.post<any>(
      'http://localhost:8080/api/auth/register',
      user,
    );
  }

  logout() {
    this.isAuthenticated.set(false);
    localStorage.removeItem('token');
  }
}
