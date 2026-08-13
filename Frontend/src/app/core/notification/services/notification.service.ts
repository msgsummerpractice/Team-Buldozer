import { inject, Service } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';
import { Notifications } from '../components/notifications/notifications';

export type NotificationData = {
  messageTranslationKey: string;
  action: string;
};

@Service()
export class NotificationService {
  private readonly snackBar = inject(MatSnackBar);

  private showSnackbar(
    messageTranslationKey: string,
    action: string,
    panelClass: string,
    hasDuration = true
  ): void {
    const config: MatSnackBarConfig = {
      horizontalPosition: 'end',
      verticalPosition: 'bottom',
      panelClass: [panelClass],
    };

    if (hasDuration) {
      config.duration = 3000;
    }

    this.snackBar.openFromComponent(Notifications, {
      data: { messageTranslationKey, action },
      duration: hasDuration ? 3000 : undefined,
      panelClass: [panelClass],
    });
  }

  showError(messageTranslationKey: string): void {
    this.showSnackbar(messageTranslationKey, 'common.close', 'error-snackbar', false);
  }

  showSuccess(messageTranslationKey: string): void {
    this.showSnackbar(messageTranslationKey, 'common.ok', 'success-snackbar');
  }

  showInfo(messageTranslationKey: string): void {
    this.showSnackbar(messageTranslationKey, 'common.ok', 'info-snackbar');
  }

  showWarning(messageTranslationKey: string): void {
    this.showSnackbar(messageTranslationKey, 'common.ok', 'warning-snackbar');
  }
}
