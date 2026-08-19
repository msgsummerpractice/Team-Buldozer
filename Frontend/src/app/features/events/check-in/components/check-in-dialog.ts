import { Component, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-check-in-dialog',
  imports: [MatButtonModule, MatDialogModule, TranslocoPipe],
  templateUrl: './check-in-dialog.html',
})
export class CheckInDialog {
  private readonly dialogRef = inject(MatDialogRef<CheckInDialog>);
  readonly dialogData = inject<{ id: number }>(MAT_DIALOG_DATA);

  protected close(): void {
    this.dialogRef.close();
  }
}
