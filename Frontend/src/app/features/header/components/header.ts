import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslocoPipe, TranslocoService } from '@jsverse/transloco';
import { AuthenticationService } from '@core/authentication/services/authentication.service';
import { AuthenticationDirective } from '@core/authentication/directives/authentication.directive';
import { RolesAssignedDirective } from '@core/authorization/directives/role-assigned.directive';
import { AuthorizationService } from '@core/authorization/services/authorization.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatTooltipModule,
    TranslocoPipe,
    AuthenticationDirective,
    RolesAssignedDirective,
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {
  private authService = inject(AuthenticationService);
  private authorization = inject(AuthorizationService);
  private router = inject(Router);
  protected translocoService = inject(TranslocoService);

  protected isAuthenticated = this.authService.isAuthenticated;

  navigateToProfile(): void {
    const id = this.authorization.getUserId();
    if (id) {
      this.router.navigate(['/info', id]);
    }
  }

  getActiveLang(): string {
    return this.translocoService.getActiveLang();
  }

  toggleLang() {
    this.translocoService.setActiveLang(this.getActiveLang() === 'ro' ? 'en' : 'ro');
  }

  logout() {
    this.authService.logout();
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
    }
  }
}
