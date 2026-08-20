import { DatePipe } from '@angular/common';
import { Component, computed, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslocoPipe } from '@jsverse/transloco';
import { EventService } from '@features/events/services/event-service';
import { EventResponse } from '@features/events/model/event-response';
import { AuthorizationService } from '@core/authorization/services/authorization.service';
import { UserRoleEnum } from '@core/users/model/user-role';
import { NotificationService } from '@core/notification/services/notification.service';
import { EventCodesDialog } from './event-codes-dialog';
import { Router } from '@angular/router';
import { EMPTY } from 'rxjs/internal/observable/empty';
import { catchError } from 'rxjs/internal/operators/catchError';

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
    MatTooltipModule,
    TranslocoPipe,
  ],
  templateUrl: './event-details-dialog.html',
})
export class EventDetailsDialog {
  private readonly dialogRef = inject(MatDialogRef<EventDetailsDialog>);
  private readonly eventService = inject(EventService);
  private readonly dialogData = inject<{ id: number }>(MAT_DIALOG_DATA);
  private readonly router = inject(Router);
  private readonly dialog = inject(MatDialog);
  private readonly authorization = inject(AuthorizationService);
  private readonly notification = inject(NotificationService);
  // Base64 prefix of a PNG file signature (\x89PNG\r\n\x1a\n)
  private readonly PNG_BASE64_PREFIX = 'iVBORw0KGgo';

  readonly event = signal<EventResponse | null>(null);
  readonly loading = signal(true);
  readonly errorTranslationKey = signal<string | null>(null);
  readonly codesGenerated = signal(false);

  protected readonly isMarketing = this.authorization.hasAnyRole([UserRoleEnum.MARKETING]);

  constructor() {
    this.eventService
      .getEventById(this.dialogData.id)
      .pipe(
        catchError((err: HttpErrorResponse) => {
          if ([401, 403, 404].includes(err.status)) {
            this.dialogRef.close();
            this.router.navigate(['/events/list']);
          } else {
            this.event.set(null);
            this.errorTranslationKey.set('event-details.dialog.error');
            this.loading.set(false);
          }
          return EMPTY;
        }),
        takeUntilDestroyed()
      )
      .subscribe({
        next: (data) => {
          this.event.set(data);
          this.codesGenerated.set(data.codesGenerated);
          this.errorTranslationKey.set(null);
          this.loading.set(false);
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

  protected canGenerateCodes(): boolean {
    const event = this.event();
    return !!event && event.status === 'PUBLISHED' && !this.codesGenerated();
  }

  protected onGenerateCodes(): void {
    const event = this.event();
    if (!event) return;
    this.eventService
      .generateCodes(event.id)
      .pipe(takeUntilDestroyed())
      .subscribe({
        next: () => {
          this.codesGenerated.set(true);
          this.notification.showSuccess('events.generate-codes-success');
          this.onViewCodes();
        },
      });
  }

  protected onViewCodes(): void {
    const event = this.event();
    if (!event) return;
    this.dialog.open(EventCodesDialog, {
      width: '440px',
      autoFocus: 'dialog',
      restoreFocus: true,
      data: { id: event.id },
    });
  }
}
