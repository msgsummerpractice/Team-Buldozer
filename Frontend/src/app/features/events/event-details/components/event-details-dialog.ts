import { DatePipe } from '@angular/common';
import { Component, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslocoPipe } from '@jsverse/transloco';
import { EventService } from '@features/events/services/event-service';
import { EventResponse } from '@features/events/model/event-response';
import { AuthorizationService } from '@core/authorization/services/authorization.service';
import { UserRoleEnum } from '@core/users/model/user-role';
import { NotificationService } from '@core/notification/services/notification.service';
import { EventCodesDialog, EventCodesDialogData } from './event-codes-dialog';
import { EventStatusEnum } from '@features/events/model/event-status';
import { EMPTY } from 'rxjs/internal/observable/empty';
import { catchError } from 'rxjs/internal/operators/catchError';

export type EventDetailsDialogData = { id: number };

@Component({
  selector: 'app-event-details-dialog',
  imports: [
    DatePipe,
    MatButtonModule,
    MatDialogModule,
    MatIconModule,
    MatTooltipModule,
    TranslocoPipe,
  ],
  templateUrl: './event-details-dialog.html',
})
export class EventDetailsDialog {
  private readonly dialogRef = inject(MatDialogRef<EventDetailsDialog>);
  private readonly eventService = inject(EventService);
  private readonly dialogData = inject<EventDetailsDialogData>(MAT_DIALOG_DATA);
  private readonly authorization = inject(AuthorizationService);

  readonly isMarketing = signal(this.authorization.hasAnyRole([UserRoleEnum.MARKETING]));
  protected readonly EventStatusEnum = EventStatusEnum;
  private readonly dialog = inject(MatDialog);
  private readonly notification = inject(NotificationService);
  // Base64 prefix of a PNG file signature (\x89PNG\r\n\x1a\n)
  private readonly PNG_BASE64_PREFIX = 'iVBORw0KGgo';

  readonly event = signal<EventResponse | null>(null);
  readonly errorTranslationKey = signal<string | null>(null);
  readonly codesGenerated = signal(false);

  readonly descriptionExpanded = signal(false);

  constructor() {
    this.eventService
      .getEventById(this.dialogData.id)
      .pipe(
        catchError((err: HttpErrorResponse) => {
          if ([401, 403, 404].includes(err.status)) {
            this.dialogRef.close();
          } else {
            this.event.set(null);
            this.errorTranslationKey.set('event-details.dialog.error');
          }
          return EMPTY;
        }),
        takeUntilDestroyed()
      )
      .subscribe((data) => {
        this.event.set(data);
        this.codesGenerated.set(data.codesGenerated);
        this.errorTranslationKey.set(null);
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

  protected readonly isCheckInDisabled = computed(() => {
    const evt = this.event();
    if (!evt) return true;
    return evt.status !== EventStatusEnum.PUBLISHED || new Date(evt.endDateTime) < new Date();
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

  protected canGenerateCodes(): boolean {
    const event = this.event();
    return !!event && event.status === EventStatusEnum.PUBLISHED && !this.codesGenerated();
  }

  protected onGenerateCodes(): void {
    const event = this.event();
    if (!event) return;
    this.eventService.generateCodes(event.id).subscribe(() => {
      this.codesGenerated.set(true);
      this.notification.showSuccess('events.generate-codes-success');
      this.onViewCodes();
    });
  }

  protected onViewCodes(): void {
    const event = this.event();
    if (!event) return;
    this.dialog.open<EventCodesDialog, EventCodesDialogData>(EventCodesDialog, {
      width: '440px',
      autoFocus: 'dialog',
      restoreFocus: true,
      data: {
        id: event.id,
        eventName: event.name,
        startDateTime: event.startDateTime,
      },
    });
  }

  protected editEvent(): void {
    this.dialogRef.close({ action: 'edit' });
  }

  protected checkIn(): void {
    this.dialogRef.close({ action: 'checkin' });
  }
}
