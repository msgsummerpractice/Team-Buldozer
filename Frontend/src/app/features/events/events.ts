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
import { combineLatest, debounceTime, distinctUntilChanged, map, Subject } from 'rxjs';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
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
import { CheckInDialog } from '@features/events/check-in/components/check-in-dialog';

const EVENT_DETAILS_DIALOG_CONFIG = {
  width: '95vw',
  maxWidth: '1400px',
  minHeight: '700px',
  maxHeight: '85vh',
};

const CHECK_IN_DIALOG_CONFIG = {
  width: '360px',
  maxWidth: '95vw',
};

interface RouteDialogState {
  id: number | null;
  routeSegment: string | null;
}

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
  protected readonly EventStatusEnum = EventStatusEnum;

  protected readonly displayedColumns = computed(() => {
    return ['name', 'period', 'status', 'type', 'location', 'actions'];
  });

  private readonly destroyRef = inject(DestroyRef);
  private searchSubject = new Subject<string>();
  private currentDialogRef: MatDialogRef<unknown> | null = null;

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

    combineLatest([this.route.paramMap, this.route.url])
      .pipe(
        debounceTime(0),
        map(([params, urlSegments]): RouteDialogState => ({
          id: params.get('id') ? +params.get('id')! : null,
          routeSegment: urlSegments[0]?.path ?? null,
        })),
        distinctUntilChanged((a, b) => a.id === b.id && a.routeSegment === b.routeSegment),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(({ id, routeSegment }) => {
        this.currentDialogRef?.close();
        this.currentDialogRef = null;

        if (!id) return;

        if (routeSegment === 'checkin') {
          this.openCheckInDialogForRoute(id);
        } else {
          this.openEventDetailsDialogForRoute(id);
        }
      });

    this.searchSubject
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntilDestroyed(this.destroyRef))
      .subscribe((term) => {
        this.searchTerm.set(term);
        this.pageIndex.set(0);
      });
  }

  private openCheckInDialogForRoute(id: number): void {
    const ref = this.dialog.open(CheckInDialog, { ...CHECK_IN_DIALOG_CONFIG, data: { id } });
    this.currentDialogRef = ref;

    ref.afterClosed().subscribe(() => {
      if (this.currentDialogRef !== ref) return;
      this.currentDialogRef = null;
      this.router.navigate(['/events/list']);
    });
  }

  private openEventDetailsDialogForRoute(id: number): void {
    const ref = this.dialog.open(EventDetailsDialog, {
      ...EVENT_DETAILS_DIALOG_CONFIG,
      data: { id },
    });
    this.currentDialogRef = ref;

    ref.afterClosed().subscribe((result?: { action: string }) => {
      if (this.currentDialogRef !== ref) return;
      this.currentDialogRef = null;

      if (result?.action === 'edit') {
        this.router.navigate(['/events', id, 'edit']);
      } else if (result?.action === 'checkin') {
        this.router.navigate(['/events', id, 'checkin']);
      } else {
        this.router.navigate(['/events/list']);
      }
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

  protected onClear(): void {
    this.searchTerm.set('');
    this.pageIndex.set(0);
  }
}
