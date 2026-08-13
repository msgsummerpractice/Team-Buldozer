import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatButtonModule } from '@angular/material/button';
import { MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { ImageCropperComponent, ImageCroppedEvent } from 'ngx-image-cropper';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-profile-picture-cropper-dialog',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatDialogModule,
    MatIconModule,
    ImageCropperComponent,
    TranslocoPipe,
  ],
  templateUrl: './profile-picture-cropper-dialog.html',
})
export class ProfilePictureCropperDialog {
  private readonly dialogRef = inject(MatDialogRef<ProfilePictureCropperDialog>);

  readonly imageChangedEvent = signal<Event | null>(null);
  readonly croppedBase64 = signal<string | null>(null);
  readonly imageLoaded = signal(false);

  fileChangeEvent(event: Event): void {
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
    this.dialogRef.close(this.croppedBase64());
  }

  cancel(): void {
    this.dialogRef.close(undefined);
  }
}
