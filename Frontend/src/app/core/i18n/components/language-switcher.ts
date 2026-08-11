import { Component, inject } from '@angular/core';
import { TranslocoService } from '@jsverse/transloco';

@Component({
  selector: 'app-language-switcher',
  standalone: true,
  template: `
    <button
      (click)="toggle()"
      class="fixed top-4 right-4 z-50 px-3 py-1.5 rounded-lg border border-gray-300 bg-white text-sm font-semibold text-gray-700 shadow hover:bg-gray-100 transition"
    >
      {{ nextLang().toUpperCase() }}
    </button>
  `,
})
export class LanguageSwitcher {
  private transloco = inject(TranslocoService);

  nextLang(): string {
    return this.transloco.getActiveLang() === 'en' ? 'ro' : 'en';
  }

  toggle(): void {
    this.transloco.setActiveLang(this.nextLang());
  }
}
