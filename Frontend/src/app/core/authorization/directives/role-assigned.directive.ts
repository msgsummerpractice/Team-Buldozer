import { Directive, effect, inject, input, TemplateRef, ViewContainerRef } from '@angular/core';
import { AuthorizationService } from '@core/authorization/services/authorization.service';
import { toSignal } from '@angular/core/rxjs-interop';
import { NavigationEnd, Router } from '@angular/router';
import { filter } from 'rxjs';

@Directive({
  selector: '[rolesAssigned]',
})
export class RolesAssignedDirective {
  private authorization = inject(AuthorizationService);
  private templateRef = inject(TemplateRef);
  private viewContainerRef = inject(ViewContainerRef);
  private router = inject(Router);

  rolesAssigned = input.required<string | string[]>();

  private routeChanged = toSignal(
    this.router.events.pipe(filter((event) => event instanceof NavigationEnd))
  );

  constructor() {
    effect(() => {
      this.routeChanged();

      const requiredRoles = this.rolesAssigned();
      const roles = Array.isArray(requiredRoles) ? requiredRoles : [requiredRoles];

      const hasAccess = this.authorization.hasAnyRole(roles);

      this.viewContainerRef.clear();
      if (hasAccess) {
        this.viewContainerRef.createEmbeddedView(this.templateRef);
      }
    });
  }
}
