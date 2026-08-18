import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, UrlTree } from '@angular/router';
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
import { UserRoleEnum } from '@core/users/model/user-role';

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
    RouterLinkActive,
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

  readonly roles = UserRoleEnum;

  protected profileLink(): UrlTree | null {
    const id = this.authorization.getUserId();
    return id ? this.router.createUrlTree(['/info', id]) : null;
  }

  protected readonly activeLang = toSignal(this.translocoService.langChanges$, {
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
