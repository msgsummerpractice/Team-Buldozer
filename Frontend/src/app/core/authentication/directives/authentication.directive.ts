import { Directive, effect, inject, TemplateRef, ViewContainerRef } from '@angular/core';
import { AuthenticationService } from '@core/authentication/services/authentication.service';

@Directive({
  selector: '[appAuth]',
})
export class AuthenticationDirective {
  private authService = inject(AuthenticationService);
  private templateRef = inject(TemplateRef);
  private viewContainerRef = inject(ViewContainerRef);

  constructor() {
    effect(() => {
      const isAuth = this.authService.isAuthenticated();
      if (isAuth) {
        this.viewContainerRef.createEmbeddedView(this.templateRef);
      } else {
        this.viewContainerRef.clear();
      }
    });
  }
}
