import { Component, effect, inject } from '@angular/core';
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
import { toSignal } from '@angular/core/rxjs-interop';

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

  protected profileLink(): [string, number] | null {
    const id = this.authorization.getUserId();
    return id ? ['/info', id] : null;
  }

  protected activeLang = toSignal(this.translocoService.langChanges$, {
    initialValue: this.translocoService.getActiveLang(),
  });

  toggleLang() {
    this.translocoService.setActiveLang(this.activeLang() === 'ro' ? 'en' : 'ro');
  }

  logout() {
    this.authService.logout();
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login']);
    }
  }
}
