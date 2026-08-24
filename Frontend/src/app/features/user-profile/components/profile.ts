import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { TranslocoPipe } from '@jsverse/transloco';
import { UserLocation, UserProfile } from '../models/userProfile.model';
import { ProfilePicture } from './profile-picture/profile-picture';
import { PageLayout } from '@shared/components/page-layout/page-layout';
import { environment } from '@environments/environment';

interface ProfileFormState {
  firstName: string;
  lastName: string;
  email: string;
  location: UserLocation;
  profilePicture: string | null;
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
    TranslocoPipe,
    ProfilePicture,
    PageLayout,
  ],
  templateUrl: './profile.html',
})
export class Profile {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private readonly apiUrl = environment.apiUrl;

  readonly profile = signal<UserProfile | null>(null);
  readonly error = signal<string>('');
  readonly saving = signal<boolean>(false);
  readonly saveMessage = signal<string>('');
  readonly editMode = signal<boolean>(false);
  readonly locationOptions: UserLocation[] = ['CLUJ', 'TIMISOARA', 'MURES'];

  formState: ProfileFormState = this.createEmptyFormState();

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
      this.error.set(
        'Could not determine the account id. Open this page with /info/:id or /info?id=yourId.'
      );
      return;
    }

    this.http.get<UserProfile>(`${this.apiUrl}/users/${userId}`).subscribe({
      next: (userProfile: UserProfile) => {
        this.profile.set(userProfile);
        this.syncFormFromProfile(userProfile);
      },
      error: () => {
        this.error.set('Could not load account details right now. Please try again.');
      },
    });
  }

  saveChanges(): void {
    const userId = this.profile()?.id ?? this.resolveUserId();

    if (!userId) {
      this.error.set('Could not determine the account id for saving changes.');
      return;
    }

    this.error.set('');
    this.saveMessage.set('');
    this.saving.set(true);

    this.http
      .patch<UserProfile>(`${this.apiUrl}/users/profile/${userId}`, this.formState)
      .subscribe({
        next: (updatedProfile: UserProfile) => {
          this.profile.set(updatedProfile);
          this.syncFormFromProfile(updatedProfile);
          this.saveMessage.set('Profile changes saved successfully.');
          this.editMode.set(false);
          this.saving.set(false);
        },
        error: (response: HttpErrorResponse) => {
          this.error.set(this.extractSaveError(response));
          this.saving.set(false);
        },
      });
  }

  enableEditMode(): void {
    this.saveMessage.set('');
    this.editMode.set(true);
  }

  cancelEditMode(): void {
    const currentProfile = this.profile();
    if (currentProfile) {
      this.syncFormFromProfile(currentProfile);
    }

    this.error.set('');
    this.saveMessage.set('');
    this.editMode.set(false);
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

  private createEmptyFormState(): ProfileFormState {
    return {
      firstName: '',
      lastName: '',
      email: '',
      location: 'CLUJ',
      profilePicture: null,
    };
  }

  private mapProfileToForm(profile: UserProfile): ProfileFormState {
    return {
      firstName: profile.firstName,
      lastName: profile.lastName,
      email: profile.email,
      location: profile.location,
      profilePicture: profile.profilePicture ?? null,
    };
  }

  onPictureChange(base64: string | null): void {
    this.formState = { ...this.formState, profilePicture: base64 };
  }

  private syncFormFromProfile(profile: UserProfile): void {
    this.formState = this.mapProfileToForm(profile);
  }

  private extractSaveError(error: HttpErrorResponse): string {
    return (
      (typeof error.error === 'string' ? error.error : error.error?.message) ??
      error.message ??
      'Could not save changes right now.'
    );
  }
}
