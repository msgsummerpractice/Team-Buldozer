import { inject, Service } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

@Service()
export class NotificationService {
  private readonly snackBar = inject(MatSnackBar);

  private showSnackbar(
    message: string,
    action: string,
    panelClass: string,
    hasDuration: boolean = true
  ): void {
    const config: MatSnackBarConfig = {
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: [panelClass],
    };

    if (hasDuration) {
      config.duration = 3000;
    }

    this.snackBar.open(message, action, config);
  }

  showError(message: string): void {
    this.showSnackbar(message, 'Close', 'error-snackbar', false);
  }

  showSuccess(message: string): void {
    this.showSnackbar(message, 'OK', 'success-snackbar');
  }

  showInfo(message: string): void {
    this.showSnackbar(message, 'OK', 'info-snackbar');
  }

  showWarning(message: string): void {
    this.showSnackbar(message, 'OK', 'warning-snackbar');
  }
}
