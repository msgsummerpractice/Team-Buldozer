import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { ImageCropperComponent, ImageCroppedEvent } from 'ngx-image-cropper';
import { TranslocoPipe } from '@jsverse/transloco';
import { NotificationService } from '@core/notification/services/notification.service';

const MAX_FILE_SIZE = 5 * 1024 * 1024;
const ALLOWED_FILE_TYPES = ['image/jpeg', 'image/png', 'image/jpg'];

@Component({
  selector: 'app-event-poster-cropper-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatDialogModule,
    MatIconModule,
    ImageCropperComponent,
    TranslocoPipe,
  ],
  templateUrl: './event-poster-cropper-dialog.html',
})
export class EventPosterCropperDialog {
  private readonly dialogRef = inject(MatDialogRef<EventPosterCropperDialog>);
  private readonly notificationService = inject(NotificationService);

  readonly imageChangedEvent = signal<Event | null>(null);
  readonly croppedBase64 = signal<string | null>(null);
  readonly imageLoaded = signal(false);

  fileChangeEvent(event: Event): void {
    const input = event.target as HTMLInputElement;
    const file = input.files?.[0];

    if (!file) {
      return;
    }

    if (!ALLOWED_FILE_TYPES.includes(file.type.toLowerCase())) {
      this.notificationService.showInfo('events.poster.errors.invalid-format');
      input.value = '';
      return;
    }

    if (file.size > MAX_FILE_SIZE) {
      this.notificationService.showInfo('events.poster.errors.size-limit');
      input.value = '';
      return;
    }

    this.croppedBase64.set(null);
    this.imageLoaded.set(false);
    this.imageChangedEvent.set(event);
  }

  onImageLoaded(): void {
    this.imageLoaded.set(true);
  }

  onImageCropped(event: ImageCroppedEvent): void {
    this.croppedBase64.set(event.base64 ?? null);
  }

  confirm(): void {
    const base64 = this.croppedBase64();
    if (base64) {
      const raw = base64.replace(/^data:image\/\w+;base64,/, '');
      const sizeInBytes = Math.floor((raw.length * 3) / 4);
      if (sizeInBytes > MAX_FILE_SIZE) {
        this.notificationService.showInfo('events.poster.errors.size-limit');
        return;
      }
    }

    this.dialogRef.close(base64);
  }

  cancel(): void {
    this.dialogRef.close(undefined);
  }
}
