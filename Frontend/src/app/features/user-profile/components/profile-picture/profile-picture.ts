import { Component, EventEmitter, Input, Output, inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { ProfilePictureCropperDialog } from './profile-picture-cropper-dialog';

@Component({
  selector: 'app-profile-picture',
  standalone: true,
  imports: [MatIconModule],
  templateUrl: './profile-picture.html',
})
export class ProfilePicture {
  private readonly dialog = inject(MatDialog);

  @Input() pictureBase64: string | null = null;
  @Input() editMode = false;
  @Output() pictureChange = new EventEmitter<string | null>();

  get pictureUrl(): string | null {
    if (!this.pictureBase64) return null;
    if (this.pictureBase64.startsWith('data:')) return this.pictureBase64;
    return `data:image/jpeg;base64,${this.pictureBase64}`;
  }

  openCropper(): void {
    const ref = this.dialog.open(ProfilePictureCropperDialog, {
      width: '520px',
      maxWidth: '95vw',
    });

    ref.afterClosed().subscribe((base64: string | undefined) => {
      if (base64) {
        const raw = base64.replace(/^data:image\/\w+;base64,/, '');
        this.pictureChange.emit(raw);
      }
    });
  }
}
