import { Component, computed, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { UserService } from '@core/users/services/user-service';
import { UserResponse } from '@core/users/dto/user.response';
import { UserRole, UserRoleEnum } from '@core/users/model/user-role';
import { debounceTime, Subject } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { TranslocoPipe } from '@jsverse/transloco';
import { UserLocationEnum } from '@core/users/model/user-location';
import { NotificationService } from '@core/notification/services/notification.service';

@Component({
  selector: 'app-users',
  imports: [
    MatIconModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatTableModule,
    MatChipsModule,
    MatPaginatorModule,
    MatSelectModule,
    MatSlideToggleModule,
    TranslocoPipe,
  ],
  templateUrl: './users.html',
  styleUrl: './users.scss',
})
export class Users implements OnInit, OnDestroy {
  private readonly userService = inject(UserService);
  private readonly notificationService = inject(NotificationService);
  private _users = signal<UserResponse[]>([]);

  protected readonly inputValue = signal<string>('');
  protected readonly searchTerm = signal<string>('');
  protected readonly pageIndex = signal<number>(0);
  protected readonly pageSize = signal<number>(5);
  protected readonly pageSizeList = [5, 10, 25, 50];

  protected readonly UserLocations = UserLocationEnum;
  protected readonly UserRoles = Object.values(UserRoleEnum) as UserRole[];

  protected readonly displayedColumns = [
    'firstName',
    'lastName',
    'email',
    'location',
    'status',
    'roles',
    'manage',
  ];

  private readonly savingIds = signal<Set<number>>(new Set());
  // plain Map avoids signal re-renders while the panel is open
  private readonly pendingRoles = new Map<number, UserRole[]>();
  private searchSubject = new Subject<string>();

  readonly filteredUsers = computed(() => {
    const allUsers = this._users();
    const term = this.searchTerm().toLowerCase().trim();
    if (!term) return allUsers;
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
    return filtered.slice(start, start + this.pageSize());
  });

  ngOnInit(): void {
    this.loadUsers();
    this.searchSubject.pipe(debounceTime(300)).subscribe((term) => {
      if (term === this.searchTerm()) return;
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

  onSearchInput(term: string): void {
    this.inputValue.set(term);
    this.searchSubject.next(term);
  }

  onPage(event: PageEvent): void {
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
  }

  protected onToggleStatus(user: UserResponse, status: boolean): void {
    this.callApi(user.id, status, user.roles);
  }

  protected onRolesClose(user: UserResponse, roles: UserRole[]): void {
    this.pendingRoles.delete(user.id);
    if (!roles?.length) return;
    this.callApi(user.id, user.status, roles);
  }

  protected setPendingRoles(userId: number, roles: UserRole[]): void {
    this.pendingRoles.set(userId, roles);
  }

  protected isLastRoleInPanel(userId: number, committedRoles: UserRole[], role: UserRole): boolean {
    const current = this.pendingRoles.get(userId) ?? committedRoles;
    return current.length === 1 && current[0] === role;
  }

  protected getRoleColor(role: string): string {
    const colors: Record<string, string> = {
      ADMIN: 'var(--color-admin)',
      HR: 'var(--color-hr)',
      MARKETING: 'var(--color-marketing)',
      PARTICIPANT: 'var(--color-participant)',
    };
    return colors[role] ?? 'var(--color-default)';
  }

  protected isSaving(userId: number): boolean {
    return this.savingIds().has(userId);
  }

  private callApi(id: number, status: boolean, roles: UserRole[]): void {
    const saving = new Set(this.savingIds());
    saving.add(id);
    this.savingIds.set(saving);

    this.userService.updateUserStatusAndRoles(id, status, roles).subscribe({
      next: (updated) => {
        this._users.update((users) => users.map((u) => (u.id === id ? updated : u)));
        const ids = new Set(this.savingIds());
        ids.delete(id);
        this.savingIds.set(ids);
        this.notificationService.showSuccess('success-messages.user-updated');
      },
      error: () => {
        const ids = new Set(this.savingIds());
        ids.delete(id);
        this.savingIds.set(ids);
      },
    });
  }
}
