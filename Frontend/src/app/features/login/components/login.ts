import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import {
  ReactiveFormsModule,
  NonNullableFormBuilder,
  Validators,
  FormsModule,
} from '@angular/forms';
import { AuthenticationService } from '@core/authentication/services/authentication.service';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, FormsModule, RouterLink, TranslocoPipe],
  templateUrl: './login.html',
})
export class Login {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly auth = inject(AuthenticationService);
  private readonly router = inject(Router);

  protected readonly loginForm = this.fb.group({
    username: ['', [Validators.required]],
    password: ['', [Validators.required, Validators.minLength(5)]],
  });

  onSubmit(): void {
    if (this.loginForm.invalid) return;

    const { username, password } = this.loginForm.getRawValue();

    this.auth.login(username, password).subscribe({
      next: () => {
        const userRole = this.auth.getUserRole();
        this.redirectByRole(userRole);
      },
      error: (err) => console.error('Login failed', err),
    });
  }

  private redirectByRole(role: string | null): void {
    if (!role) {
      this.router.navigate(['/login']);
      return;
    }

    const roleRoutes: { [key: string]: string } = {
      participant: '/home/participant',
      marketing: '/home/marketing',
      hr: '/home/hr',
      admin: '/home/admin',
    };

    const route = roleRoutes[role.toLowerCase()] || '/';
    this.router.navigate([route]);
  }
}
