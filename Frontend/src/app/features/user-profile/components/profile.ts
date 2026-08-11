import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { UserProfile } from '../models/userProfile.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './profile.html',
})
export class Profile {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);

  readonly profile = signal<UserProfile | null>(null);
  readonly loading = signal<boolean>(true);
  readonly error = signal<string>('');

  readonly fullName = computed(() => {
    const currentProfile = this.profile();
    if (!currentProfile) return '';
    return `${currentProfile.firstName} ${currentProfile.lastName}`.trim();
  });

  constructor() {
    this.loadProfile();
  }

  private loadProfile(): void {
    const userId = this.resolveUserId();

    if (!userId) {
      this.loading.set(false);
      this.error.set(
        'Could not determine the account id. Open this page with /info/:id or /info?id=yourId.'
      );
      return;
    }

    this.http.get<UserProfile>(`http://localhost:8080/api/v1/users/${userId}`).subscribe({
      next: (userProfile: UserProfile) => {
        this.profile.set(userProfile);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Could not load account details right now. Please try again.');
        this.loading.set(false);
      },
    });
  }

  private resolveUserId(): number | null {
    const pathId = this.route.snapshot.paramMap.get('id');
    const queryId = this.route.snapshot.queryParamMap.get('id');
    const storedId = localStorage.getItem('userId');
    const tokenId = this.getUserIdFromToken(localStorage.getItem('token'));

    return (
      this.toNumericId(pathId) ?? this.toNumericId(queryId) ?? this.toNumericId(storedId) ?? tokenId
    );
  }

  private toNumericId(value: string | null): number | null {
    if (!value) return null;
    const id = Number(value);
    if (!Number.isInteger(id) || id <= 0) return null;
    return id;
  }

  private getUserIdFromToken(token: string | null): number | null {
    if (!token || !token.includes('.')) return null;

    try {
      const payloadBase64 = token.split('.')[1];
      const payloadJson = atob(payloadBase64.replace(/-/g, '+').replace(/_/g, '/'));
      const payload = JSON.parse(payloadJson) as Record<string, unknown>;

      const candidate = payload['userId'] ?? payload['id'] ?? payload['uid'] ?? null;
      if (typeof candidate === 'number' && Number.isInteger(candidate) && candidate > 0) {
        return candidate;
      }

      if (typeof candidate === 'string') {
        return this.toNumericId(candidate);
      }

      return null;
    } catch {
      return null;
    }
  }
}
