import {Component, inject, OnInit, signal} from '@angular/core';
import {UserService} from '@core/users/services/user-service';
import {UserResponse} from '@core/users/dto/user.response';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {MatButton, MatIconButton} from '@angular/material/button';
import {debounceTime, distinctUntilChanged, Subject} from 'rxjs';
import {MatIcon} from '@angular/material/icon';
import {RouterLink} from '@angular/router';
import {TranslocoPipe} from '@jsverse/transloco';
import {MatTooltip} from '@angular/material/tooltip';

@Component({
  selector: 'app-users',
  imports: [
    CommonModule,
    FormsModule,
    MatButton,
    MatIcon,
    MatIconButton,
    RouterLink,
    TranslocoPipe,
    MatTooltip
  ],
  templateUrl: './users.html',
  styleUrl: './users.css',
})
export class Users implements OnInit {

  private readonly userService = inject(UserService);
  private _users = signal<UserResponse[]>([]);
  private _filteredUsers = signal<UserResponse[]>([]);

  readonly users = this._filteredUsers.asReadonly();

  searchTerm = signal<string>('');
  pageIndex = signal<number>(0);
  pageSize = signal<number>(10);

  private searchSubject = new Subject<string>();

  ngOnInit() {
    this.loadUsers();

    this.searchSubject.pipe(
      debounceTime(300),
      distinctUntilChanged()
    ).subscribe(term => {
      this.searchTerm.set(term);
      this.pageIndex.set(0);
      this.applyFilters()
    })
  }

  loadUsers(): void {
    this.userService.getAllUsers().subscribe({
      next: (data) => {
        this._users.set(data);
        this.applyFilters();
      },
      error: (err) => {
        console.error(err);
      }
    })
  }

  onSearch(term: string): void {
    this.searchSubject.next(term);
  }

  onPageChange(pageIndex: number): void {
    this.pageIndex.set(pageIndex);
  }

  private applyFilters(): void {
    let filtered = this._users();

    const term = this.searchTerm().toLowerCase();
    if (term) {
      filtered = filtered.filter(user =>
        user.firstName.toLowerCase().includes(term) ||
        user.lastName.toLowerCase().includes(term) ||
        user.email.toLowerCase().includes(term) ||
        user.location?.toString().toLowerCase().includes(term) ||
        user.roles?.some(role => role.toLowerCase().includes(term))
      );
    }

    this._filteredUsers.set(filtered);
  }

  get paginatedUsers(): UserResponse[] {
    const start = this.pageIndex() * this.pageSize();
    const end = start + this.pageSize();
    return this.users().slice(start, end);
  }

  ngOnDestroy(): void {
    this.searchSubject.complete();
  }

}
