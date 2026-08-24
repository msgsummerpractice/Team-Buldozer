import { Component, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { TranslocoPipe } from '@jsverse/transloco';
import { EMPTY } from 'rxjs/internal/observable/empty';
import { catchError } from 'rxjs/internal/operators/catchError';

import { EventService } from '@features/events/services/event-service';
import { EventQrPdfService } from '@features/events/services/event-qr-pdf-service';
import { EventCodesResponse } from '@features/events/model/event-codes-response';

export type EventCodesDialogData = {
  id: number;
  eventName?: string;
  startDateTime?: string;
  poster?: string;
};

@Component({
  selector: 'app-event-codes-dialog',
  imports: [MatButtonModule, MatDialogModule, MatDividerModule, MatIconModule, TranslocoPipe],
  templateUrl: './event-codes-dialog.html',
})
export class EventCodesDialog {
  private readonly dialogRef = inject(MatDialogRef<EventCodesDialog>);
  private readonly eventService = inject(EventService);
  private readonly eventQrPdfService = inject(EventQrPdfService);
  private readonly dialogData = inject<EventCodesDialogData>(MAT_DIALOG_DATA);

  readonly codes = signal<EventCodesResponse | null>(null);
  readonly errorKey = signal<string | null>(null);

  protected readonly qrCodeSrc = computed(() => {
    const qr = this.codes()?.qrCode;

    return qr ? `data:image/png;base64,${qr}` : '';
  });

  constructor() {
    this.eventService
      .getEventCodes(this.dialogData.id)
      .pipe(
        catchError(() => {
          this.errorKey.set('event-codes.dialog.error');
          return EMPTY;
        })
      )
      .subscribe((data) => {
        this.codes.set(data);
      });
  }

  protected async downloadQr(): Promise<void> {
    const codes = this.codes();

    if (!codes) {
      return;
    }

    const { id, eventName, startDateTime, poster } = this.dialogData;

    await this.eventQrPdfService.download({ id, codes, eventName, startDateTime, poster });
  }

  protected close(): void {
    this.dialogRef.close();
  }
}
