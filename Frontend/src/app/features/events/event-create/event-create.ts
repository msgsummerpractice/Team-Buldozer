import { Component, DestroyRef, inject, OnInit, signal } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { EventPosterCropperDialog } from './components/event-poster-cropper-dialog';
import { EventService } from '@features/events/services/event-service';
import {
  FormControl,
  FormGroup,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { Location } from '@angular/common';
import { EventType, EventTypeEnum } from '@features/events/model/event-type';
import { EventLocation, EventLocationEnum } from '@features/events/model/event-location';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { EventRequest } from '@features/events/model/event-request';
import { EventUpdateRequest } from '@features/events/model/event-update-request';
import { EventResponse } from '@features/events/model/event-response';
import { NotificationService } from '@core/notification/services/notification.service';
import { MatCard, MatCardContent } from '@angular/material/card';
import { MatError, MatFormField, MatInput, MatLabel, MatSuffix } from '@angular/material/input';
import { MatIcon } from '@angular/material/icon';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatDivider } from '@angular/material/list';
import { MatButton, MatIconButton, MatMiniFabButton } from '@angular/material/button';
import { MatTooltip } from '@angular/material/tooltip';
import { provideNativeDateAdapter } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatTimepickerModule } from '@angular/material/timepicker';
import { TranslocoPipe } from '@jsverse/transloco';
import { PageLayout } from '@shared/components/page-layout/page-layout';
import {
  combineDateAndTime,
  eventDateRangeValidator,
  futureEventStartValidator,
  futureRegistrationStartValidator,
  registrationDateRangeValidator,
} from '@features/events/event-create/utils/event-utils';
import { catchError, distinctUntilChanged, EMPTY } from 'rxjs';

interface EventFormControls {
  name: FormControl<string>;
  type: FormControl<EventType>;
  location: FormControl<EventLocation>;
  startDate: FormControl<Date | null>;
  startTime: FormControl<Date | null>;
  endDate: FormControl<Date | null>;
  endTime: FormControl<Date | null>;
  registrationStartDate: FormControl<Date | null>;
  registrationEndDate: FormControl<Date | null>;
  address: FormControl<string>;
  description: FormControl<string>;
  foodProvided: FormControl<boolean | null>;
}

@Component({
  selector: 'app-event-create',
  providers: [provideNativeDateAdapter()],
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
    MatIconButton,
    MatTooltip,
    MatInput,
    MatButton,
    MatDatepickerModule,
    MatTimepickerModule,
    RouterLink,
    TranslocoPipe,
    MatSuffix,
    PageLayout,
  ],
  templateUrl: './event-create.html',
})
export class EventCreate implements OnInit {
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly eventService = inject(EventService);
  private readonly notificationService = inject(NotificationService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly dialog = inject(MatDialog);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly location = inject(Location);

  readonly selectedFile = signal<File | null>(null);
  readonly posterBase64 = signal<string | null>(null);
  readonly imagePreview = signal<string | null>(null);
  readonly showFoodProvided = signal(true);
  readonly submitting = signal(false);
  readonly eventId = signal<number | null>(null);
  readonly isEditMode = signal(false);
  readonly posterChanged = signal(false);

  readonly maxFileSize = 5 * 1024 * 1024;
  readonly allowedFileTypes = ['image/jpeg', 'image/png', 'image/jpg'];
  readonly today = new Date();
  readonly maxDate = new Date(new Date().getFullYear(), 11, 31);

  readonly eventTypes: EventType[] = Object.values(EventTypeEnum);
  readonly eventType = EventTypeEnum;
  readonly availableLocations = signal<EventLocation[]>([
    EventLocationEnum.CLUJ,
    EventLocationEnum.TIMISOARA,
    EventLocationEnum.MURES,
  ]);

  eventForm: FormGroup<EventFormControls> = this.fb.group<EventFormControls>({
    name: this.fb.control('', [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(256),
    ]),
    type: this.fb.control<EventType>(EventTypeEnum.LOCAL, Validators.required),
    location: this.fb.control<EventLocation>(EventLocationEnum.CLUJ, Validators.required),
    startDate: this.fb.control<Date | null>(null, [Validators.required, futureEventStartValidator]),
    startTime: this.fb.control<Date | null>(null, [Validators.required, futureEventStartValidator]),
    endDate: this.fb.control<Date | null>(null, [Validators.required, eventDateRangeValidator]),
    endTime: this.fb.control<Date | null>(null, [Validators.required, eventDateRangeValidator]),
    registrationStartDate: this.fb.control<Date | null>(null, [
      Validators.required,
      futureRegistrationStartValidator,
    ]),
    registrationEndDate: this.fb.control<Date | null>(null, [
      Validators.required,
      registrationDateRangeValidator,
    ]),
    address: this.fb.control('', [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(128),
    ]),
    description: this.fb.control('', [
      Validators.required,
      Validators.minLength(3),
      Validators.maxLength(1024),
    ]),
    foodProvided: this.fb.control<boolean | null>(false),
  });

  ngOnInit() {
    this.setupTypeChangeSubscription();
    this.setupDateRangeRevalidation();
    this.handleTypeChange(this.eventType.LOCAL);

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      if (!Number.isNaN(id)) {
        this.eventId.set(id);
        this.isEditMode.set(true);
        this.loadEvent(id);
      }
    }
  }

  private loadEvent(id: number): void {
    this.eventService
      .getEventById(id)
      .pipe(
        catchError(() => {
          this.router.navigate(['/events']);
          return EMPTY;
        })
      )
      .subscribe(this.populateForm.bind(this));
  }

  private populateForm(event: EventResponse): void {
    // Backend sends UTC ISO 8601 strings; new Date() converts them to local time for the form controls.
    const startDate = new Date(event.startDateTime);
    const endDate = new Date(event.endDateTime);

    this.eventForm.patchValue({
      name: event.name,
      type: event.type,
      location: event.location,
      startDate,
      startTime: startDate,
      endDate,
      endTime: endDate,
      registrationStartDate: new Date(event.registrationStartDate),
      registrationEndDate: new Date(event.registrationEndDate),
      address: event.address,
      description: event.description,
      foodProvided: event.foodProvided,
    });

    this.handleTypeChange(event.type);

    if (event.poster) {
      const dataUrl = event.poster.startsWith('data:')
        ? event.poster
        : `data:image/jpeg;base64,${event.poster}`;
      this.imagePreview.set(dataUrl);
      this.posterBase64.set(event.poster.replace(/^data:image\/\w+;base64,/, ''));
      this.selectedFile.set(this.dataUrlToFile(dataUrl, 'poster.jpeg'));
    }

    this.posterChanged.set(false);
  }

  private setupDateRangeRevalidation(): void {
    const startDate = this.eventForm.get('startDate');
    const startTime = this.eventForm.get('startTime');
    const endDate = this.eventForm.get('endDate');
    const endTime = this.eventForm.get('endTime');
    const registrationEndDate = this.eventForm.get('registrationEndDate');

    ['startDate', 'startTime', 'endDate', 'endTime'].forEach((name) => {
      this.eventForm
        .get(name)
        ?.valueChanges.pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe(() => {
          if (name !== 'startDate') {
            startDate?.updateValueAndValidity({ emitEvent: false });
          }
          if (name !== 'startTime') {
            startTime?.updateValueAndValidity({ emitEvent: false });
          }
          if (name !== 'endDate') {
            endDate?.updateValueAndValidity({ emitEvent: false });
          }
          if (name !== 'endTime') {
            endTime?.updateValueAndValidity({ emitEvent: false });
          }
        });
    });

    this.eventForm
      .get('registrationStartDate')
      ?.valueChanges.pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        registrationEndDate?.updateValueAndValidity({ emitEvent: false });
      });
  }

  private setupTypeChangeSubscription(): void {
    this.eventForm
      .get('type')
      ?.valueChanges.pipe(takeUntilDestroyed(this.destroyRef), distinctUntilChanged())
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
      case EventTypeEnum.INTERNAL:
        this.availableLocations.set([EventLocationEnum.ALL]);
        locationControl.setValue(EventLocationEnum.ALL);
        locationControl.disable();
        this.showFoodProvided.set(true);
        if (foodControl.value === null) {
          foodControl.setValue(false);
        }
        break;

      case EventTypeEnum.LOCAL:
        this.availableLocations.set([
          EventLocationEnum.CLUJ,
          EventLocationEnum.TIMISOARA,
          EventLocationEnum.MURES,
        ]);
        locationControl.enable();
        if (locationControl.value === EventLocationEnum.ALL || !locationControl.value) {
          locationControl.setValue(EventLocationEnum.CLUJ);
        }
        locationControl.setValidators([Validators.required]);
        locationControl.updateValueAndValidity();

        this.showFoodProvided.set(true);
        if (foodControl.value === null) {
          foodControl.setValue(false);
        }
        break;

      case EventTypeEnum.EXTERNAL:
        this.availableLocations.set([
          EventLocationEnum.CLUJ,
          EventLocationEnum.TIMISOARA,
          EventLocationEnum.MURES,
        ]);
        locationControl.enable();
        if (locationControl.value === EventLocationEnum.ALL || !locationControl.value) {
          locationControl.setValue(EventLocationEnum.CLUJ);
        }
        locationControl.setValidators([Validators.required]);
        locationControl.updateValueAndValidity();

        this.showFoodProvided.set(false);
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
      this.notificationService.showInfo('events.poster.errors.invalid-format');
      input.value = '';
      return;
    }

    if (file.size > this.maxFileSize) {
      this.notificationService.showInfo('events.poster.errors.size-limit');
      input.value = '';
      return;
    }

    const reader = new FileReader();
    reader.onload = () => {
      const dataUrl = reader.result as string;
      this.imagePreview.set(dataUrl);
      this.posterBase64.set(dataUrl.split(',')[1]);
      this.selectedFile.set(file);
      this.posterChanged.set(true);
    };
    reader.readAsDataURL(file);
  }

  openPosterCropper(): void {
    const ref = this.dialog.open(EventPosterCropperDialog, {
      width: '720px',
      maxWidth: '95vw',
    });

    ref.afterClosed().subscribe((base64: string | undefined) => {
      if (!base64) {
        return;
      }

      const raw = base64.replace(/^data:image\/\w+;base64,/, '');
      const dataUrl = base64.startsWith('data:') ? base64 : `data:image/jpeg;base64,${raw}`;

      this.imagePreview.set(dataUrl);
      this.posterBase64.set(raw);
      this.selectedFile.set(this.dataUrlToFile(dataUrl, 'poster.jpeg'));
      this.posterChanged.set(true);
    });
  }

  private dataUrlToFile(dataUrl: string, filename: string): File {
    const [meta, base64] = dataUrl.split(',');
    const mimeMatch = meta.match(/data:(.*);base64/);
    const mime = mimeMatch?.[1] ?? 'image/jpeg';
    const binary = atob(base64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) {
      bytes[i] = binary.charCodeAt(i);
    }
    return new File([bytes], filename, { type: mime });
  }

  removePoster(): void {
    this.selectedFile.set(null);
    this.imagePreview.set(null);
    this.posterBase64.set(null);
    this.posterChanged.set(true);
  }

  goBack(): void {
    this.location.back();
  }

  onSubmit(): void {
    if (this.eventForm.invalid || this.submitting()) {
      this.eventForm.markAllAsTouched();
      return;
    }

    const {
      foodProvided,
      location,
      startDate,
      startTime,
      endDate,
      endTime,
      registrationStartDate,
      registrationEndDate,
      ...rest
    } = this.eventForm.getRawValue();

    const start = combineDateAndTime(startDate, startTime);
    const end = combineDateAndTime(endDate, endTime);

    // All dates are converted to UTC ISO 8601 strings before being sent to the backend.
    const basePayload = {
      ...rest,
      startDateTime: start ? new Date(start).toISOString() : '',
      endDateTime: end ? new Date(end).toISOString() : '',
      registrationStartDate: registrationStartDate
        ? new Date(registrationStartDate).toISOString()
        : '',
      registrationEndDate: registrationEndDate ? new Date(registrationEndDate).toISOString() : '',
      location: location || undefined,
      foodProvided: foodProvided ?? undefined,
    };

    this.submitting.set(true);

    if (this.isEditMode()) {
      const id = this.eventId();
      if (id === null) {
        this.submitting.set(false);
        return;
      }

      const updateRequest: EventUpdateRequest = { ...basePayload };
      if (this.posterChanged()) {
        updateRequest.poster = this.posterBase64() ?? '';
      }

      this.eventService
        .updateEvent(id, updateRequest)
        .pipe(
          catchError(() => {
            this.submitting.set(false);
            return EMPTY;
          })
        )
        .subscribe(() => {
          this.notificationService.showSuccess('events.messages.update-success');
          this.submitting.set(false);
          this.posterChanged.set(false);
        });

      return;
    }

    const request: EventRequest = {
      ...basePayload,
      poster: this.posterBase64() ?? undefined,
    };

    this.eventService
      .addEvent(request)
      .pipe(
        catchError(() => {
          this.submitting.set(false);
          return EMPTY;
        })
      )
      .subscribe(() => {
        this.notificationService.showSuccess('events.messages.success');
        this.submitting.set(false);
        this.router.navigate(['/events']);
      });
  }
}
