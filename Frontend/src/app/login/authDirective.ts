import { Directive, effect, inject, TemplateRef, ViewContainerRef } from '@angular/core';
import { Authentication } from './authService';

@Directive({
  selector: '[appAuth]',
})
export class AuthDirective {
  private authService = inject(Authentication);
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
