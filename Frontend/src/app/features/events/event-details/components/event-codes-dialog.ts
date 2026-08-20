import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslocoPipe } from '@jsverse/transloco';
import { EventService } from '@features/events/services/event-service';
import { EventCodesResponse } from '@features/events/model/event-codes-response';
import { finalize } from 'rxjs/internal/operators/finalize';

@Component({
  selector: 'app-event-codes-dialog',
  imports: [
    MatButtonModule,
    MatDialogModule,
    MatDividerModule,
    MatIconModule,
    MatProgressSpinnerModule,
    TranslocoPipe,
  ],
  templateUrl: './event-codes-dialog.html',
})
export class EventCodesDialog {
  private readonly dialogRef = inject(MatDialogRef<EventCodesDialog>);
  private readonly eventService = inject(EventService);
  private readonly dialogData = inject<{ id: number }>(MAT_DIALOG_DATA);

  readonly codes = signal<EventCodesResponse | null>(null);
  readonly loading = signal(true);
  readonly errorKey = signal<string | null>(null);
  protected readonly qrCodeSrc = computed(() => {
    const qr = this.codes()?.qrCode;
    return qr ? `data:image/png;base64,${qr}` : '';
  });

  constructor() {
    this.eventService
      .getEventCodes(this.dialogData.id)
      .pipe(
        takeUntilDestroyed(),
        finalize(() => this.loading.set(false))
      )
      .subscribe({
        next: (data) => this.codes.set(data),
        error: () => this.errorKey.set('event-codes.dialog.error'),
      });
  }

  protected close(): void {
    this.dialogRef.close();
  }
}
