import { Component, computed, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslocoPipe, TranslocoService } from '@jsverse/transloco';
import { AuthenticationService } from '@core/authentication/services/authentication.service';
import { AuthenticationDirective } from '@core/authentication/directives/authentication.directive'; // Importă directiva

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
  private authentification = inject(AuthenticationService);
  protected translocoService = inject(TranslocoService);

  protected readonly isAuthenticated = this.authentification.isAuthenticated;

  protected nextLang() {
    return this.translocoService.getActiveLang() === 'en' ? 'ro' : 'en';
  }

  toggleLang() {
    this.translocoService.setActiveLang(this.nextLang());
  }

  logout() {
    this.authentification.logout();
  }
}
