import { Component, inject, signal } from '@angular/core';
import {
  ReactiveFormsModule,
  NonNullableFormBuilder,
  Validators,
  AbstractControl,
  ValidationErrors,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { TranslocoPipe } from '@jsverse/transloco';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, TranslocoPipe, MatIconModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSelectModule],
  templateUrl: './register.html',
  styleUrl: './register.scss',
})
export class Register {
  private fb = inject(NonNullableFormBuilder);
  private router = inject(Router);

  private _success = signal(false);
  private _serverErrorKey = signal('');

  readonly showPassword = signal(false);
  readonly showConfirmPassword = signal(false);

  readonly success = this._success.asReadonly();
  readonly serverErrorKey = this._serverErrorKey.asReadonly();

  get emailControl() {
    return this.registerForm.controls.email;
  }

  registerForm = this.fb.group(
    {
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required],
      location: ['', Validators.required],
    },
    { validators: this.passwordMatchValidator }
  );

  onSubmit() {
    if (this.registerForm.invalid) return;

    this._serverErrorKey.set('');

    const { confirmPassword, ...payload } = this.registerForm.getRawValue();

    this.auth.register(payload).subscribe({
      next: () => {
        this._success.set(true);
        setTimeout(() => this.router.navigate(['/login']), 2000);
      },
      error: (err) => {
        this._serverErrorKey.set(
          err.status === 409 ? 'register.errors.duplicate-email' : 'register.errors.server-error'
        );
      },
    });
  }

  private passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
    const password = group.get('password')?.value;
    const confirm = group.get('confirmPassword')?.value;
    return password === confirm ? null : { passwordMismatch: true };
  }
}
