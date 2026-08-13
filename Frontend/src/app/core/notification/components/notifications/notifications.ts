import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { NotificationData } from '@core/notification/services/notification.service';
import { TranslocoService } from '@jsverse/transloco';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-notifications',
  imports: [MatButtonModule],
  templateUrl: './notifications.html',
})
export class Notifications {
  data = inject<NotificationData>(MAT_SNACK_BAR_DATA);
  snackBarRef = inject(MatSnackBarRef);
  private translocoService = inject(TranslocoService);

  message = signal<string>('');
  actionText = signal<string>('');

  constructor() {
    this.updateTranslations();

    this.translocoService.langChanges$.pipe(takeUntilDestroyed()).subscribe(() => {
      this.updateTranslations();
    });
  }

  private updateTranslations(): void {
    this.message.set(this.translocoService.translate(this.data.messageTranslationKey));
    this.actionText.set(this.translocoService.translate(this.data.action));
  }

  close(): void {
    this.snackBarRef.dismissWithAction();
  }
}
