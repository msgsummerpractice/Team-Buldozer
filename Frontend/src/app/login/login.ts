import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import {
  ReactiveFormsModule,
  NonNullableFormBuilder,
  Validators,
  FormsModule,
} from '@angular/forms';
import { Authentication } from './authService';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, CommonModule, RouterLink],
  templateUrl: './login.html',
})
export class Login {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly auth = inject(Authentication);
  private readonly router = inject(Router);

  showMfaStep = signal<boolean>(false);
  activeUsername = signal<string>('');
  mfaCode = '';

  protected readonly loginForm = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required, Validators.minLength(5)]],
  });

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    const { username, password } = this.loginForm.getRawValue();

    this.auth.login(username, password).subscribe({
      next: (res) => {
        if (res.mfaRequired) {
          this.activeUsername.set(username);
          this.showMfaStep.set(true);
        }
      },
      error: (err) => console.error('Login failed', err),
    });
  }

  onVerifyMfa(): void {
    if (!this.mfaCode) return;

    this.auth.verifyMfa(this.activeUsername(), this.mfaCode).subscribe({
      next: () => this.router.navigate(['/info']),
      error: (err) => console.error('MFA verification failed', err),
    });
  }
}
