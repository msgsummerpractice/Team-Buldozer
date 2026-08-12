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
})
export class Header {
  private authService = inject(AuthenticationService);
  private router = inject(Router);
  protected translocoService = inject(TranslocoService);

  protected get isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  protected nextLang() {
    return this.translocoService.getActiveLang() === 'en' ? 'ro' : 'en';
  }

  toggleLang() {
    this.translocoService.setActiveLang(this.nextLang());
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
