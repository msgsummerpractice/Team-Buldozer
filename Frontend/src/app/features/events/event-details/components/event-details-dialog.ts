import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslocoPipe } from '@jsverse/transloco';
import { EventService } from '@features/events/services/event-service';
import { EventResponse } from '@features/events/model/event-response';
import { Router } from '@angular/router';

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
  private readonly dialogData = inject<{ id: number }>(MAT_DIALOG_DATA);
  private readonly router = inject(Router);

  // Base64 prefix of a PNG file signature (\x89PNG\r\n\x1a\n)
  private readonly PNG_BASE64_PREFIX = 'iVBORw0KGgo';

  readonly event = signal<EventResponse | null>(null);
  readonly loading = signal(true);
  readonly errorTranslationKey = signal<string | null>(null);

  constructor() {
    this.eventService
      .getEventById(this.dialogData.id)
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: (data) => {
          this.event.set(data);
          this.errorTranslationKey.set(null);
          this.loading.set(false);
        },
        error: (err: HttpErrorResponse) => {
          if (err.status === 403 || err.status === 401 || err.status === 404) {
            this.dialogRef.close();
            this.router.navigate(['/events/list']);
          } else {
            this.event.set(null);
            this.errorTranslationKey.set('event-details.dialog.error');
            this.loading.set(false);
          }
        },
      });
  }

  protected readonly statusKey = computed(() => {
    const status = this.event()?.status;
    return status ? `event-details.dialog.status-${status.toLowerCase()}` : '';
  });

  protected readonly typeKey = computed(() => {
    const type = this.event()?.type;
    return type ? `event-details.dialog.type-${type.toLowerCase()}` : '';
  });

  protected readonly posterUrl = computed(() => {
    const poster = this.event()?.poster;
    if (!poster) {
      return null;
    }

    const mime = poster.startsWith(this.PNG_BASE64_PREFIX) ? 'image/png' : 'image/jpeg';
    return `data:${mime};base64,${poster}`;
  });

  protected close(): void {
    this.dialogRef.close();
  }
}
