import { Component, computed, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { TranslocoPipe } from '@jsverse/transloco';
import { jsPDF } from 'jspdf';
import { EventService } from '@features/events/services/event-service';
import { EventCodesResponse } from '@features/events/model/event-codes-response';
import { EMPTY } from 'rxjs/internal/observable/empty';
import { catchError } from 'rxjs/internal/operators/catchError';

export type EventCodesDialogData = { id: number };

@Component({
  selector: 'app-event-codes-dialog',
  imports: [MatButtonModule, MatDialogModule, MatDividerModule, MatIconModule, TranslocoPipe],
  templateUrl: './event-codes-dialog.html',
})
export class EventCodesDialog {
  private readonly dialogRef = inject(MatDialogRef<EventCodesDialog>);
  private readonly eventService = inject(EventService);
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
      .subscribe((data) => this.codes.set(data));
  }

  protected downloadQr(): void {
    const codes = this.codes();
    if (!codes) return;
    const doc = new jsPDF({ orientation: 'portrait', unit: 'mm', format: 'a4' });
    const qrSize = 80;
    const pageWidth = doc.internal.pageSize.getWidth();
    const x = (pageWidth - qrSize) / 2;
    doc.addImage(`data:image/png;base64,${codes.qrCode}`, 'PNG', x, 20, qrSize, qrSize);
    doc.save(`check-in-qr-${this.dialogData.id}.pdf`);
  }

  protected close(): void {
    this.dialogRef.close();
  }
}
