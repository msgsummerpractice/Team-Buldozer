import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { UserService } from '@core/users/services/user-service';
import { UserResponse } from '@core/users/dto/user.response';
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
import { UserLocation, UserLocationEnum } from '@core/users/model/user-location';

@Component({
  selector: 'app-users',
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
  templateUrl: './users.html',
})
export class Users implements OnInit {
  private readonly userService = inject(UserService);
  private _users = signal<UserResponse[]>([]);

  protected readonly searchTerm = signal<string>('');
  protected readonly pageIndex = signal<number>(0);
  protected readonly pageSize = signal<number>(5);
  protected readonly pageSizeList = [5, 10, 25, 50];

  protected readonly UserLocations = UserLocationEnum;

  protected readonly displayedColumns = [
    'firstName',
    'lastName',
    'email',
    'location',
    'status',
    'roles',
    'manage',
  ];

  private searchSubject = new Subject<string>();

  readonly filteredUsers = computed(() => {
    const allUsers = this._users();
    const term = this.searchTerm().toLowerCase().trim();

    if (!term) {
      return allUsers;
    }

    return allUsers.filter(
      (user) =>
        user.firstName.toLowerCase().includes(term) ||
        user.lastName.toLowerCase().includes(term) ||
        user.email.toLowerCase().includes(term) ||
        user.location?.toString().toLowerCase().includes(term) ||
        user.roles?.some((role) => role.toLowerCase().includes(term))
    );
  });

  readonly paginatedUsers = computed(() => {
    const filtered = this.filteredUsers();
    const start = this.pageIndex() * this.pageSize();
    const end = start + this.pageSize();

    return filtered.slice(start, end);
  });

  ngOnInit() {
    this.loadUsers();

    this.searchSubject.pipe(debounceTime(300), distinctUntilChanged()).subscribe((term) => {
      this.searchTerm.set(term);
      this.pageIndex.set(0);
    });
  }

  ngOnDestroy(): void {
    this.searchSubject.complete();
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe((data) => this._users.set(data));
  }

  onSearch(term: string): void {
    this.searchSubject.next(term);
  }

  onPage(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
  }
}
