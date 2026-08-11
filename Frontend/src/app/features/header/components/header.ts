import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslocoPipe, TranslocoService, provideTranslocoScope } from '@jsverse/transloco';
import { AuthenticationService } from '@core/authentication/services/authentication.service';

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
  ],
  providers: [provideTranslocoScope('nav')],
  templateUrl: './header.html',
})
export class Header {
  protected translocoService = inject(TranslocoService);
  protected authentification = inject(AuthenticationService);

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
