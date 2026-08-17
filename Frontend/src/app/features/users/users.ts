import { Component, computed, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { UserService } from '@core/users/services/user-service';
import { UserResponse } from '@core/users/dto/user.response';
import { UserRole, UserRoleEnum } from '@core/users/model/user-role';
import { FormsModule } from '@angular/forms';
import { debounceTime, Subject } from 'rxjs';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSelectModule } from '@angular/material/select';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { TranslocoPipe, TranslocoService } from '@jsverse/transloco';
import { UserLocationEnum } from '@core/users/model/user-location';
import { NotificationService } from '@core/notification/services/notification.service';

type PendingEdit = { status: boolean; roles: UserRole[] };

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
    MatSelectModule,
    MatSlideToggleModule,
    TranslocoPipe,
  ],
  templateUrl: './users.html',
})
export class Users implements OnInit, OnDestroy {
  private readonly userService = inject(UserService);
  private readonly notificationService = inject(NotificationService);
  private readonly translocoService = inject(TranslocoService);
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

  private readonly pendingEdits = signal<Map<number, PendingEdit>>(new Map());
  private readonly savingIds = signal<Set<number>>(new Set());

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

    this.searchSubject.pipe(debounceTime(300)).subscribe((term) => {
      if (term === this.searchTerm()) return;
      if (!this.confirmDiscardChanges()) {
        this.inputValue.set(this.searchTerm());
        return;
      }
      this.pendingEdits.set(new Map());
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
    if (!this.confirmDiscardChanges()) return;
    this.pendingEdits.set(new Map());
    this.pageIndex.set(event.pageIndex);
    this.pageSize.set(event.pageSize);
  }

  protected isEdited(userId: number): boolean {
    return this.pendingEdits().has(userId);
  }

  protected isSaving(userId: number): boolean {
    return this.savingIds().has(userId);
  }

  protected getEditStatus(user: UserResponse): boolean {
    return this.pendingEdits().get(user.id)?.status ?? user.status;
  }

  protected getEditRoles(user: UserResponse): UserRole[] {
    return this.pendingEdits().get(user.id)?.roles ?? [...user.roles];
  }

  protected onStatusChange(user: UserResponse, status: boolean): void {
    const map = new Map(this.pendingEdits());
    const current = map.get(user.id) ?? { status: user.status, roles: [...user.roles] };
    map.set(user.id, { ...current, status });
    this.pendingEdits.set(map);
  }

  protected onRolesChange(user: UserResponse, roles: UserRole[]): void {
    const map = new Map(this.pendingEdits());
    const current = map.get(user.id) ?? { status: user.status, roles: [...user.roles] };
    map.set(user.id, { ...current, roles });
    this.pendingEdits.set(map);
  }

  protected saveUser(user: UserResponse): void {
    const edit = this.pendingEdits().get(user.id);
    if (!edit) return;

    const saving = new Set(this.savingIds());
    saving.add(user.id);
    this.savingIds.set(saving);

    this.userService.updateUserStatusAndRoles(user.id, edit.status, edit.roles).subscribe({
      next: (updated) => {
        this._users.update((users) => users.map((u) => (u.id === user.id ? updated : u)));
        const edits = new Map(this.pendingEdits());
        edits.delete(user.id);
        this.pendingEdits.set(edits);
        const ids = new Set(this.savingIds());
        ids.delete(user.id);
        this.savingIds.set(ids);
        this.notificationService.showSuccess('success-messages.user-updated');
      },
      error: () => {
        const ids = new Set(this.savingIds());
        ids.delete(user.id);
        this.savingIds.set(ids);
      },
    });
  }

  private confirmDiscardChanges(): boolean {
    if (this.pendingEdits().size === 0) return true;
    return window.confirm(this.translocoService.translate('users.unsaved-changes'));
  }
}
