import { DatePipe } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslocoPipe } from '@jsverse/transloco';
import { EventService } from '@core/events/services/event-service';
import { EventResponse } from '@core/events/model/event-response';

@Component({
  selector: 'app-event-details-dialog',
  imports: [
    DatePipe,
    MatButtonModule,
    MatChipsModule,
    MatDialogModule,
    MatDividerModule,
    MatIconModule,
    MatProgressSpinnerModule,
    TranslocoPipe,
  ],
  templateUrl: './event-details-dialog.html',
})
export class EventDetailsDialog {
  private readonly dialogRef = inject(MatDialogRef<EventDetailsDialog>);
  private readonly eventService = inject(EventService);
  private readonly eventId = inject<number>(MAT_DIALOG_DATA);

  readonly event = signal<EventResponse | null>(null);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  constructor() {
    this.eventService.getEventById(this.eventId).subscribe({
      next: (data) => {
        this.event.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('event-details.dialog.error');
        this.loading.set(false);
      },
    });
  }

  protected get statusKey(): string {
    const status = this.event()?.status;
    return status ? `event-details.dialog.status-${status.toLowerCase()}` : '';
  }

  protected get typeKey(): string {
    const type = this.event()?.type;
    return type ? `event-details.dialog.type-${type.toLowerCase()}` : '';
  }

  close(): void {
    this.dialogRef.close();
  }
}
