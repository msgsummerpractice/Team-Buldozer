import { Component, computed, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { EventService } from '@features/events/services/event-service';
import { EventResponse } from '@features/events/model/event-response';
import { EventStatusEnum } from '@features/events/model/event-status';
import { AuthorizationService } from '@core/authorization/services/authorization.service';
import { UserRoleEnum } from '@core/users/model/user-role';
import { NotificationService } from '@core/notification/services/notification.service';
import {
  CompleteEventDialog,
  CompleteEventDialogData,
} from '@features/events/components/complete-event-dialog/complete-event-dialog';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';
import { MatDialog } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTooltipModule } from '@angular/material/tooltip';
import { TranslocoPipe } from '@jsverse/transloco';
import { EventDetailsDialog } from '@features/events/event-details/components/event-details-dialog';
import {
  PublishEventDialog,
  PublishEventDialogData,
} from '@features/events/components/publish-event-dialog/publish-event-dialog';

const EVENT_DETAILS_DIALOG_CONFIG = {
  width: '95vw',
  maxWidth: '1400px',
  minHeight: '85vh',
};

@Component({
  selector: 'app-events',
  imports: [
    FormsModule,
    MatIconModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatTableModule,
    MatChipsModule,
    MatPaginatorModule,
    MatTooltipModule,
    TranslocoPipe,
    RouterLink,
  ],
  templateUrl: './events.html',
})
export class Events implements OnInit {
  private readonly eventService = inject(EventService);
  private readonly authorization = inject(AuthorizationService);
  private readonly notification = inject(NotificationService);
  private readonly dialog = inject(MatDialog);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private _events = signal<EventResponse[]>([]);

  protected readonly searchTerm = signal<string>('');
  protected readonly pageIndex = signal<number>(0);
  protected readonly pageSize = signal<number>(5);
  protected readonly pageSizeList = [5, 10, 25, 50];
  protected readonly sortDirection = signal<'asc' | 'desc'>('asc');

  protected readonly isMarketing = signal(this.authorization.hasAnyRole([UserRoleEnum.MARKETING]));

  protected readonly displayedColumns = computed(() => {
    return ['name', 'period', 'status', 'type', 'location', 'actions'];
  });

  private readonly destroyRef = inject(DestroyRef);
  private searchSubject = new Subject<string>();

  readonly filteredEvents = computed(() => {
    const allEvents = this._events();
    const term = this.searchTerm().toLowerCase().trim();
    const dir = this.sortDirection();

    const filtered = !term
      ? [...allEvents]
      : allEvents.filter(
          (event) =>
            event.name.toLowerCase().includes(term) ||
            event.status.toLowerCase().includes(term) ||
            event.type.toLowerCase().includes(term) ||
            event.location.toLowerCase().includes(term)
        );

    return filtered.sort((a, b) => {
      const diff = new Date(a.startDateTime).getTime() - new Date(b.startDateTime).getTime();
      return dir === 'asc' ? diff : -diff;
    });
  });

  readonly paginatedEvents = computed(() => {
    const filtered = this.filteredEvents();
    const start = this.pageIndex() * this.pageSize();
    const end = start + this.pageSize();
    return filtered.slice(start, end);
  });

  ngOnInit(): void {
    this.loadEvents();
    this.route.parent!.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params) => {
      const idParam = params.get('id');
      const id: number | null = idParam ? +idParam : null;
      if (id) {
        this.dialog
          .open(EventDetailsDialog, { ...EVENT_DETAILS_DIALOG_CONFIG, data: { id } })
          .afterClosed()
          .subscribe(() => this.router.navigate(['/events/list']));
      }
    });

    this.searchSubject
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntilDestroyed(this.destroyRef))
      .subscribe((term) => {
        this.searchTerm.set(term);
        this.pageIndex.set(0);
      });
  }

  protected loadEvents(): void {
    this.eventService.getAllEvents().subscribe((data) => this._events.set(data));
  }

  protected onSearch(term: string): void {
    this.searchSubject.next(term);
  }

  protected onPage(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
  }

  protected toggleSort(): void {
    this.sortDirection.update((d) => (d === 'asc' ? 'desc' : 'asc'));
    this.pageIndex.set(0);
  }

  protected formatPeriod(start: string, end: string): string {
    const fmt = (dt: string) => {
      const d = new Date(dt);
      return d.toLocaleDateString('en-GB', { day: '2-digit', month: '2-digit', year: 'numeric' });
    };
    return `${fmt(start)} - ${fmt(end)}`;
  }

  protected canComplete(event: EventResponse): boolean {
    return (
      event.status === EventStatusEnum.PUBLISHED &&
      new Date(event.endDateTime).getTime() <= Date.now()
    );
  }

  protected canPublish(event: EventResponse): boolean {
    return (
      event.status === EventStatusEnum.DRAFT && new Date(event.endDateTime).getTime() > Date.now()
    );
  }

  protected onComplete(event: EventResponse): void {
    if (!this.canComplete(event)) return;

    const dialogRef = this.dialog.open<CompleteEventDialog, CompleteEventDialogData, boolean>(
      CompleteEventDialog,
      {
        data: { eventName: event.name },
        width: '440px',
        autoFocus: 'dialog',
        restoreFocus: true,
      }
    );

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;
      this.eventService.completeEvent(event.id).subscribe((updated) => {
        this._events.update((list) => list.map((e) => (e.id === updated.id ? updated : e)));
        this.notification.showSuccess('events.complete-success');
      });
    });
  }

  protected onPublish(event: EventResponse): void {
    if (!this.canPublish(event)) return;

    const dialogRef = this.dialog.open<PublishEventDialog, PublishEventDialogData, boolean>(
      PublishEventDialog,
      {
        data: { eventName: event.name },
        width: '440px',
        autoFocus: 'dialog',
        restoreFocus: true,
      }
    );

    dialogRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;
      this.eventService.publishEvent(event.id).subscribe((updated) => {
        this._events.update((list) => list.map((e) => (e.id === updated.id ? updated : e)));
        this.notification.showSuccess('events.publish-success');
      });
    });
  }

  protected onClear(): void {
    this.searchTerm.set('');
    this.pageIndex.set(0);
  }
}
