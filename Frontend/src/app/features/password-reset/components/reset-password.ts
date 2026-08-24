import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslocoPipe } from '@jsverse/transloco';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { HttpErrorResponse } from '@angular/common/http';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { catchError, EMPTY } from 'rxjs';
import { PasswordResetService } from '../services/password-reset.service';
import { NotificationService } from '@core/notification/services/notification.service';
import { LoadingService } from '@core/loading/services/loading.service';

@Component({
  selector: 'app-reset-password',
  imports: [
    ReactiveFormsModule,
    TranslocoPipe,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    RouterLink,
  ],
  templateUrl: './reset-password.html',
})
export class ResetPassword {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly passwordResetService = inject(PasswordResetService);
  private readonly notification = inject(NotificationService);
  protected readonly loading = inject(LoadingService).isLoading;

  protected readonly showPassword = signal(false);
  protected readonly showConfirmPassword = signal(false);
  protected readonly tokenError = signal<string | null>(null);

  private readonly token = this.route.snapshot.queryParamMap.get('token');

  constructor() {
    if (!this.token) {
      this.router.navigate(['/forgot-password']);
      return;
    }

    this.passwordResetService
      .validateToken(this.token)
      .pipe(
        takeUntilDestroyed(),
        catchError((error: HttpErrorResponse) => {
          const code: string = error.error?.code;
          this.tokenError.set(`server-error-codes.${code ?? 'ERR-99'}`);
          this.resetForm.disable();
          return EMPTY;
        })
      )
      .subscribe();
  }

  protected readonly resetForm = this.fb.group({
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(128)]],
    confirmPassword: [
      '',
      [Validators.required, Validators.minLength(8), Validators.maxLength(128)],
    ],
  });

  protected onSubmit(): void {
    if (this.resetForm.invalid) return;

    const { password, confirmPassword } = this.resetForm.getRawValue();
    if (password !== confirmPassword) {
      this.resetForm.get('confirmPassword')?.setErrors({ passwordMismatch: true });
      return;
    } else if (this.resetForm.get('confirmPassword')?.hasError('passwordMismatch')) {
      this.resetForm.get('confirmPassword')?.setErrors(null);
    }

    this.tokenError.set(null);
    this.passwordResetService
      .resetPassword(this.token!, password, confirmPassword)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          const code: string = error.error?.code;
          this.tokenError.set(`server-error-codes.${code ?? 'ERR-99'}`);
          return EMPTY;
        })
      )
      .subscribe(() => {
        this.notification.showSuccess('password-reset.resetSuccess');
        this.router.navigate(['/login']);
      });
  }
}
