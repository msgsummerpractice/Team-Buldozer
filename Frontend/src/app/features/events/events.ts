import { Component, computed, inject, OnInit, signal, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { EventService } from '@core/events/services/event-service';
import { EventResponse } from '@core/events/model/event-response';
import { FormsModule } from '@angular/forms';
import { debounceTime, distinctUntilChanged, Subject } from 'rxjs';
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
  ],
  templateUrl: './events.html',
})
export class Events implements OnInit {
  private readonly eventService = inject(EventService);
  private _events = signal<EventResponse[]>([]);

  protected readonly searchTerm = signal<string>('');
  protected readonly pageIndex = signal<number>(0);
  protected readonly pageSize = signal<number>(5);
  protected readonly pageSizeList = [5, 10, 25, 50];
  protected readonly sortDirection = signal<'asc' | 'desc'>('asc');

  protected readonly displayedColumns = ['name', 'period', 'status', 'type', 'location', 'details'];

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

    this.searchSubject
      .pipe(debounceTime(300), distinctUntilChanged(), takeUntilDestroyed(this.destroyRef))
      .subscribe((term) => {
        this.searchTerm.set(term);
        this.pageIndex.set(0);
      });
  }

  loadEvents(): void {
    this.eventService.getAllEvents().subscribe((data) => this._events.set(data));
  }

  onSearch(term: string): void {
    this.searchSubject.next(term);
  }

  onPage(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
  }

  toggleSort(): void {
    this.sortDirection.update((d) => (d === 'asc' ? 'desc' : 'asc'));
    this.pageIndex.set(0);
  }

  formatPeriod(start: string, end: string): string {
    const fmt = (dt: string) => {
      const d = new Date(dt);
      return d.toLocaleDateString('en-GB', { day: '2-digit', month: '2-digit', year: 'numeric' });
    };
    return `${fmt(start)} - ${fmt(end)}`;
  }
  onClear(): void {
    this.searchTerm.set('');
    this.pageIndex.set(0);
  }
}
