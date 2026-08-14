import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { ReactiveFormsModule, NonNullableFormBuilder, Validators } from '@angular/forms';
import { AuthenticationService } from '@core/authentication/services/authentication.service';
import { TranslocoPipe } from '@jsverse/transloco';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

@Component({
  selector: 'app-login',
  imports: [
    ReactiveFormsModule,
    TranslocoPipe,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    RouterLink,
  ],
  templateUrl: './login.html',
})
export class Login {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly auth = inject(AuthenticationService);

  protected readonly isLoading = signal(false);
  protected readonly showPassword = signal(false);
  protected readonly loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
  });

  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }

    this.isLoading.set(true);
    const { email, password } = this.loginForm.getRawValue();

    this.auth
      .login(email, password)
      .pipe(finalize(() => this.isLoading.set(false)))
      .subscribe();
  }
}
