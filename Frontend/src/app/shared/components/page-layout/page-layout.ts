import { ChangeDetectionStrategy, Component, input } from '@angular/core';

@Component({
  selector: 'app-page-layout',
  templateUrl: './page-layout.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PageLayout {
  readonly title = input.required<string>();
  readonly subtitle = input<string>('');
  readonly image = input<string>('/assets/images/LVL_4811.jpg');
}
