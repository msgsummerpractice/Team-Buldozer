import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { AuthenticationService } from '@core/authentication/services/authentication.service';
import { TranslocoPipe, TranslocoService } from '@jsverse/transloco';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    MatButtonModule,
    MatToolbarModule,
    MatIconModule,
    MatTooltipModule,
    RouterLink,
    TranslocoPipe,
  ],
  templateUrl: './home.html',
})
export class Home {
  protected authentification = inject(AuthenticationService);
  private transloco = inject(TranslocoService);

  nextLang(): string {
    return this.transloco.getActiveLang() === 'en' ? 'ro' : 'en';
  }

  toggleLang(): void {
    this.transloco.setActiveLang(this.nextLang());
  }

  logout() {
    this.authentification.logout();
  }
}
