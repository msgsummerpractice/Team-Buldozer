import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { TranslocoPipe } from '@jsverse/transloco';

export interface PublishEventDialogData {
  eventName: string;
}

@Component({
  selector: 'app-publish-event-dialog',
  imports: [MatButtonModule, MatDialogModule, MatIconModule, TranslocoPipe],
  templateUrl: './publish-event-dialog.html',
})
export class PublishEventDialog {
  private readonly dialogRef = inject(MatDialogRef<PublishEventDialog, boolean>);
  protected readonly data = inject<PublishEventDialogData>(MAT_DIALOG_DATA);

  confirm(): void {
    this.dialogRef.close(true);
  }

  cancel(): void {
    this.dialogRef.close(false);
  }
}
