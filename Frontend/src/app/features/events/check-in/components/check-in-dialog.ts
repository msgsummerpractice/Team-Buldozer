import { Component, ElementRef, OnDestroy, inject, signal, viewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { TranslocoPipe } from '@jsverse/transloco';
import { BrowserQRCodeReader, IScannerControls } from '@zxing/browser';
import { NotificationService } from '@core/notification/services/notification.service';
import { EventService } from '@features/events/services/event-service';

export function parseCheckInCode(
  code: string
): { eventId: number | null; eventName: string | null } | null {
  const composedMatch = /^(\d+)-(.+)$/.exec(code);
  if (composedMatch) {
    return { eventId: Number(composedMatch[1]), eventName: composedMatch[2] };
  }

  const simpleMatch = /^\d{6}$/.exec(code);
  if (simpleMatch) {
    return { eventId: null, eventName: null };
  }

  return null;
}

@Component({
  selector: 'app-check-in-dialog',
  imports: [
    FormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    TranslocoPipe,
  ],
  templateUrl: './check-in-dialog.html',
})
export class CheckInDialog implements OnDestroy {
  private readonly videoEl = viewChild.required<ElementRef<HTMLVideoElement>>('videoEl');

  private readonly dialogRef = inject(MatDialogRef<CheckInDialog>);
  readonly dialogData = inject<{ id: number }>(MAT_DIALOG_DATA);
  private readonly eventService = inject(EventService);
  private readonly notificationService = inject(NotificationService);

  protected checkInCode = signal('');
  protected cameraError = signal<string | null>(null);
  protected scanning = signal(false);
  protected submitting = signal(false);

  private scannerControls: IScannerControls | null = null;
  private readonly codeReader = new BrowserQRCodeReader();

  protected async startScan(): Promise<void> {
    this.cameraError.set(null);
    this.scanning.set(true);
    try {
      this.scannerControls = await this.codeReader.decodeFromConstraints(
        { video: { facingMode: 'environment' } },
        this.videoEl().nativeElement,
        (result) => {
          if (result) {
            this.stopScan();
            this.processCode(result.getText());
          }
        }
      );
    } catch {
      this.scanning.set(false);
      this.cameraError.set('check-in.camera-denied');
    }
  }

  protected stopScan(): void {
    this.scannerControls?.stop();
    this.scannerControls = null;
    this.scanning.set(false);
  }

  protected processCode(rawCode: string): void {
    const code = rawCode.trim();
    const parsed = parseCheckInCode(code);

    if (parsed === null) {
      this.notificationService.showError('check-in.invalid-code');
      return;
    }

    if (parsed.eventId !== null && parsed.eventId !== this.dialogData.id) {
      this.notificationService.showError('check-in.wrong-event');
      return;
    }

    this.submit(this.dialogData.id, code);
  }

  protected confirmCode(): void {
    this.processCode(this.checkInCode());
  }

  private submit(eventId: number, code: string): void {
    this.submitting.set(true);
    this.eventService.checkInEvent(eventId, code).subscribe({
      next: () => {
        this.notificationService.showSuccess('check-in.success');
        this.dialogRef.close(true);
      },
      error: () => {
        this.notificationService.showError('check-in.failed');
        this.submitting.set(false);
      },
    });
  }

  protected close(): void {
    this.dialogRef.close();
  }

  ngOnDestroy(): void {
    this.stopScan();
  }
}
