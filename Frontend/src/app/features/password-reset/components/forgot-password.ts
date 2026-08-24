import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TranslocoPipe } from '@jsverse/transloco';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { PasswordResetService } from '../services/password-reset.service';
import { NotificationService } from '@core/notification/services/notification.service';
import { LoadingService } from '@core/loading/services/loading.service';

@Component({
  selector: 'app-forgot-password',
  imports: [
    ReactiveFormsModule,
    TranslocoPipe,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    RouterLink,
  ],
  templateUrl: './forgot-password.html',
})
export class ForgotPassword {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly router = inject(Router);
  private readonly passwordResetService = inject(PasswordResetService);
  private readonly notification = inject(NotificationService);
  protected readonly loading = inject(LoadingService).isLoading;

  protected readonly emailForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
  });

  protected onSubmit(): void {
    if (this.emailForm.invalid) return;

    const { email } = this.emailForm.getRawValue();
    this.passwordResetService.forgotPassword(email).subscribe(() => {
      this.notification.showInfo('password-reset.emailSent');
      this.router.navigate(['/login']);
    });
  }
}
