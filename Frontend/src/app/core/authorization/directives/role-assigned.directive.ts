import { Directive, effect, inject, input, TemplateRef, ViewContainerRef } from '@angular/core';
import { AuthorizationService } from '@core/authorization/services/authorization.service';

@Directive({
  selector: '[rolesAssigned]',
  standalone: true,
})
export class RolesAssignedDirective {
  private authorization = inject(AuthorizationService);
  private templateRef = inject(TemplateRef);
  private viewContainerRef = inject(ViewContainerRef);

  rolesAssigned = input.required<string | string[]>();

  constructor() {
    effect(() => {
      const requiredRoles = this.rolesAssigned();
      const roles = Array.isArray(requiredRoles) ? requiredRoles : [requiredRoles];

      const hasAccess = this.authorization.hasAnyRole(roles);

      if (hasAccess) {
        this.viewContainerRef.createEmbeddedView(this.templateRef);
      } else {
        this.viewContainerRef.clear();
      }
    });
  }
}
