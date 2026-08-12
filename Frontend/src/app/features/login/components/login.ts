import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import {
  ReactiveFormsModule,
  NonNullableFormBuilder,
  Validators,
  FormsModule,
} from '@angular/forms';
import { AuthenticationService } from '@core/authentication/services/authentication.service';
import { TranslocoPipe } from '@jsverse/transloco';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, TranslocoPipe],
  templateUrl: './login.html',
})
export class Login {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly auth = inject(AuthenticationService);
  private readonly router = inject(Router);
  private readonly snackBar = inject(MatSnackBar);

  protected readonly isLoading = signal(false);
  protected readonly loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(5)]],
  });

  onSubmit(): void {
    if (this.loginForm.invalid) {
      Object.keys(this.loginForm.controls).forEach((key) => {
        this.loginForm.get(key)?.markAsTouched();
      });
      return;
    }

    if (this.isLoading()) return;

    this.isLoading.set(true);
    const { email, password } = this.loginForm.getRawValue();

    this.auth.login(email, password).subscribe({
      next: () => {
        const userRole = this.auth.getUserRole();
        const route = this.getRoleRoute(userRole);
        this.router.navigate([route]);
      },
      error: (err) => {
        console.error('Login error:', err);
        const message = this.getErrorMessage(err);
        this.snackBar.open(message, 'OK', {
          duration: 0,
          horizontalPosition: 'center',
          verticalPosition: 'bottom',
          panelClass: ['error-snackbar'],
        });
        this.isLoading.set(false);
      },
    });
  }

  private getErrorMessage(err: any): string {
    console.log('Error object:', err);

    if (err?.error?.message) {
      return err.error.message;
    }

    if (err?.status === 401 || err?.status === 403) {
      return 'Invalid email or password.';
    }

    if (err?.status === 0) {
      return 'Cannot connect to server. Please check your connection.';
    }

    if (err?.status === 500) {
      return 'Server error. Please try again later.';
    }

    return 'Invalid email or password. Please try again.';
  }

  private getRoleRoute(role: string | null): string {
    const routes: Record<string, string> = {
      participant: '/home/participant',
      marketing: '/home/marketing',
      hr: '/home/hr',
      admin: '/home/admin',
    };
    return routes[role?.toLowerCase() || ''] || '/';
  }

  protected isFieldInvalid(fieldName: string): boolean {
    const field = this.loginForm.get(fieldName);
    return !!(field && field.invalid && (field.dirty || field.touched));
  }
}
