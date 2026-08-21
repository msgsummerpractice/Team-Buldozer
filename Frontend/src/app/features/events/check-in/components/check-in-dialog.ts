import { Component, ElementRef, OnDestroy, inject, signal, viewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { TranslocoPipe } from '@jsverse/transloco';
import { BrowserQRCodeReader, IScannerControls } from '@zxing/browser';
import { EMPTY, catchError } from 'rxjs';
import { NotificationService } from '@core/notification/services/notification.service';
import { AttendanceService } from '@features/events/check-in/services/attendance-service';

type CheckInCodeParseResult =
  | {
      codeType: 'QR';
      eventId: number;
      eventName: string;
    }
  | {
      codeType: 'digits';
    };

function parseCheckInCode(code: string): CheckInCodeParseResult | null {
  const composedMatch = /^(\d+)-(.+)$/.exec(code);
  if (composedMatch) {
    return { codeType: 'QR', eventId: Number(composedMatch[1]), eventName: composedMatch[2] };
  }

  const simpleMatch = /^\d{6}$/.exec(code);
  if (simpleMatch) {
    return { codeType: 'digits' };
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
  private readonly attendanceService = inject(AttendanceService);
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

    if (parsed.codeType === 'QR') {
      if (parsed.eventId !== this.dialogData.id) {
        this.notificationService.showError('check-in.wrong-event');
        return;
      }
      this.submitQrCode(parsed.eventId, parsed.eventName);
      return;
    }

    this.submitCode(code);
  }

  protected confirmCode(): void {
    this.processCode(this.checkInCode());
  }

  private submitCode(checkInCode: string): void {
    this.submitting.set(true);
    this.attendanceService
      .checkInByCode(checkInCode)
      .pipe(
        catchError(() => {
          this.submitting.set(false);
          return EMPTY;
        })
      )
      .subscribe(() => this.onCheckInSuccess());
  }

  private submitQrCode(eventId: number, eventName: string): void {
    this.submitting.set(true);
    this.attendanceService
      .checkInByQrCode(eventId, eventName)
      .pipe(
        catchError(() => {
          this.submitting.set(false);
          return EMPTY;
        })
      )
      .subscribe(() => this.onCheckInSuccess());
  }

  private onCheckInSuccess(): void {
    this.notificationService.showSuccess('check-in.success');
    this.dialogRef.close(true);
  }

  protected close(): void {
    this.dialogRef.close();
  }

  ngOnDestroy(): void {
    this.stopScan();
  }
}
