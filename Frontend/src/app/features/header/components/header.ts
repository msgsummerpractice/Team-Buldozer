import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslocoPipe, TranslocoService } from '@jsverse/transloco';
import { AuthenticationService } from '@core/authentication/services/authentication.service';
import { AuthenticationDirective } from '@core/authentication/directives/authentication.directive';

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
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {
  private authService = inject(AuthenticationService);
  private router = inject(Router);
  protected translocoService = inject(TranslocoService);

  protected isAuthenticated = this.authService.isAuthenticated;

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
