import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { TranslocoPipe } from '@jsverse/transloco';

export interface CompleteEventDialogData {
  eventName: string;
}

@Component({
  selector: 'app-complete-event-dialog',
  standalone: true,
  imports: [MatButtonModule, MatDialogModule, MatIconModule, TranslocoPipe],
  templateUrl: './complete-event-dialog.html',
})
export class CompleteEventDialog {
  private readonly dialogRef = inject(MatDialogRef<CompleteEventDialog, boolean>);
  protected readonly data = inject<CompleteEventDialogData>(MAT_DIALOG_DATA);

  confirm(): void {
    this.dialogRef.close(true);
  }

  cancel(): void {
    this.dialogRef.close(false);
  }
}
