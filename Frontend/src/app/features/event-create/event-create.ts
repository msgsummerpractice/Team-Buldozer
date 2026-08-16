import { ChangeDetectorRef, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { EventService } from '@core/events/services/event-service';
import {
  AbstractControl,
  FormGroup,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { EventType } from '@core/events/model/event-type';
import { EventLocation } from '@core/events/model/event-location';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { EventRequest } from '@core/events/dto/event-request';
import { NotificationService } from '@core/notification/services/notification.service';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatError, MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/list';
import { MatButton, MatMiniFabButton } from '@angular/material/button';
import { TranslocoPipe } from '@jsverse/transloco';

@Component({
  selector: 'app-event-create',
  imports: [
    MatCard,
    MatCardContent,
    MatFormField,
    MatLabel,
    MatIcon,
    MatError,
    MatSelect,
    MatOption,
    MatCheckbox,
    MatDivider,
    ReactiveFormsModule,
    MatMiniFabButton,
    MatInput,
    MatButton,
    RouterLink,
    TranslocoPipe,
  ],
  templateUrl: './event-create.html',
  styleUrl: './event-create.scss',
})
export class EventCreate implements OnInit {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly eventService = inject(EventService);
  private readonly router = inject(Router);
  private readonly notificationService = inject(NotificationService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly cdr = inject(ChangeDetectorRef);

  selectedFile: File | null = null;
  posterBase64: string | null = null;
  imagePreview: string | null = null;

  readonly maxFileSize = 5 * 1024 * 1024;
  readonly allowedFileTypes = ['image/jpeg', 'image/png', 'image/jpg'];

  readonly eventTypes: EventType[] = ['LOCAL', 'EXTERNAL', 'INTERNAL'];
  readonly cities: EventLocation[] = ['ALL', 'MURES', 'TIMISOARA', 'CLUJ'];

  showFoodProvided = true;

  eventForm: FormGroup = this.fb.group(
    {
      name: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(256)]],
      type: ['LOCAL', Validators.required],
      location: ['CLUJ', Validators.required],
      startDateTime: ['', Validators.required],
      endDateTime: ['', Validators.required],
      registrationStartDate: ['', Validators.required],
      registrationEndDate: ['', Validators.required],
      address: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(128)]],
      description: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(1024)]],
      foodProvided: [false],
    },
    {
      validators: [this.dateRangeValidator, this.registrationDateRangeValidator],
    }
  );

  ngOnInit() {
    this.setupTypeChangeSubscription();
    this.handleTypeChange('LOCAL');
  }

  private setupTypeChangeSubscription(): void {
    this.eventForm
      .get('type')
      ?.valueChanges.pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((selectedType: EventType) => {
        this.handleTypeChange(selectedType);
      });
  }

  private handleTypeChange(type: EventType): void {
    const locationControl = this.eventForm.get('location');
    const foodControl = this.eventForm.get('foodProvided');

    if (!locationControl || !foodControl) {
      return;
    }

    switch (type) {
      case 'INTERNAL':
        locationControl.setValue('ALL');
        locationControl.disable();
        this.showFoodProvided = true;
        if (foodControl.value === null) {
          foodControl.setValue(false);
        }
        break;

      case 'LOCAL':
        locationControl.enable();
        if (locationControl.value === 'ALL' || !locationControl.value) {
          locationControl.setValue('');
        }
        locationControl.setValidators([Validators.required]);
        locationControl.updateValueAndValidity();

        this.showFoodProvided = true;
        if (foodControl.value === null) {
          foodControl.setValue(false);
        }
        break;

      case 'EXTERNAL':
        locationControl.enable();
        if (locationControl.value === 'ALL' || !locationControl.value) {
          locationControl.setValue('');
        }
        locationControl.setValidators([Validators.required]);
        locationControl.updateValueAndValidity();

        this.showFoodProvided = false;
        foodControl.setValue(null);
        break;
    }
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) {
      return;
    }

    const file = input.files[0];

    if (!this.allowedFileTypes.includes(file.type.toLowerCase())) {
      this.notificationService.showInfo('events.invalid-poster-format');
      input.value = '';
      return;
    }

    if (file.size > this.maxFileSize) {
      this.notificationService.showInfo('events.poster-size');
      input.value = '';
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      const dataUrl = reader.result as string;
      this.imagePreview = dataUrl;

      this.posterBase64 = dataUrl.split(',')[1];
      this.cdr.markForCheck();
    };
    reader.readAsDataURL(file);
  }

  removePoster(): void {
    this.selectedFile = null;
    this.imagePreview = null;
    this.posterBase64 = null;
    this.cdr.markForCheck();
  }

  onSubmit(): void {
    if (this.eventForm.invalid) {
      this.eventForm.markAllAsTouched();
      return;
    }

    const request: EventRequest = {
      ...this.eventForm.getRawValue(),
      poster: this.posterBase64,
    };

    this.eventService.addEvent(request).subscribe({
      next: (createdEvent) => {
        this.notificationService.showSuccess('events.success');
        // this.router.navigate(['/events', createdEvent.id]);
      },
      error: (errorResponse) => {
        const errorMessage = errorResponse?.error?.message || 'events.failed';
        this.notificationService.showError(errorMessage);
        console.log(errorResponse?.error?.message);
      },
    });
  }

  private dateRangeValidator(group: AbstractControl): ValidationErrors | null {
    const start = group.get('startDateTime')?.value;
    const end = group.get('endDateTime')?.value;

    if (start && end && new Date(end) <= new Date(start)) {
      return { invalidEventDates: true };
    }
    return null;
  }

  private registrationDateRangeValidator(group: AbstractControl): ValidationErrors | null {
    const start = group.get('registrationStartDate')?.value;
    const end = group.get('registrationEndDate')?.value;

    if (start && end && new Date(end) < new Date(start)) {
      return { invalidRegistrationDates: true };
    }
    return null;
  }
}
