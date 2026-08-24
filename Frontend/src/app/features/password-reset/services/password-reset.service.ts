import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';

@Service()
export class PasswordResetService {
  private readonly http = inject(HttpClient);

  forgotPassword(email: string): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/auth/forgot-password`, { email });
  }

  validateToken(token: string): Observable<void> {
    return this.http.get<void>(`${environment.apiUrl}/auth/validate-token`, { params: { token } });
  }

  resetPassword(
    token: string,
    newPassword: string,
    newPasswordConfirmation: string
  ): Observable<void> {
    return this.http.post<void>(`${environment.apiUrl}/auth/reset-password`, {
      token,
      newPassword,
      newPasswordConfirmation,
    });
  }
}
