import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslocoPipe } from '@jsverse/transloco';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { PasswordResetService } from '../services/password-reset.service';
import { NotificationService } from '@core/notification/services/notification.service';

@Component({
  selector: 'app-password-reset',
  imports: [
    ReactiveFormsModule,
    TranslocoPipe,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    RouterLink,
  ],
  templateUrl: './password-reset.html',
})
export class PasswordReset {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly passwordResetService = inject(PasswordResetService);
  private readonly notification = inject(NotificationService);

  protected readonly showPassword = signal(false);
  protected readonly showConfirmPassword = signal(false);

  protected readonly mode = signal<'forgot' | 'reset'>(
    this.route.snapshot.queryParamMap.has('token') ? 'reset' : 'forgot'
  );

  protected readonly emailForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  protected readonly resetForm = this.fb.group({
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required]],
  });

  protected onForgotSubmit(): void {
    if (this.emailForm.invalid) return;

    const { email } = this.emailForm.getRawValue();
    this.passwordResetService.forgotPassword(email).subscribe(() => {
      this.notification.showInfo('password-reset.emailSent');
      this.router.navigate(['/login']);
    });
  }

  protected onResetSubmit(): void {
    if (this.resetForm.invalid) return;

    const { password, confirmPassword } = this.resetForm.getRawValue();
    if (password !== confirmPassword) {
      this.notification.showError('password-reset.passwordMismatch');
      return;
    }

    const token = this.route.snapshot.queryParamMap.get('token')!;
    this.passwordResetService.resetPassword(token, password).subscribe(() => {
      this.notification.showSuccess('password-reset.resetSuccess');
      this.router.navigate(['/login']);
    });
  }
}
