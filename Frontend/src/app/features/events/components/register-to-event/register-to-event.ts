import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import {
  FormControl,
  FormGroup,
  NonNullableFormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { TranslocoPipe } from '@jsverse/transloco';
import { EMPTY, catchError, finalize } from 'rxjs';
import { NotificationService } from '@core/notification/services/notification.service';
import { ConfirmDialog, ConfirmDialogData } from '@shared/components/confirm-dialog/confirm-dialog';
import { EventResponse } from '@features/events/model/event-response';
import { EventTypeEnum } from '@features/events/model/event-type';
import { FoodPreference, FoodPreferenceEnum } from '@features/events/model/food-preference';
import { RegistrationRequest } from '@features/events/model/registration-request';
import { RegistrationResponse } from '@features/events/model/registration-response';
import { RegistrationService } from '@features/events/services/registration-service';

export type RegisterToEventDialogMode = 'register' | 'edit';

export type RegisterToEventDialogData = {
  event: EventResponse;
  mode?: RegisterToEventDialogMode;
};

interface RegistrationFormControls {
  gdprConsent: FormControl<boolean>;
  photoConsent: FormControl<boolean>;
  foodPreference: FormControl<FoodPreference | null>;
  transportNeeded: FormControl<boolean>;
  driverName: FormControl<string>;
  driverPhoneNumber: FormControl<string>;
  accommodationNeeded: FormControl<boolean>;
  accommodationDays: FormControl<number>;
}

@Component({
  selector: 'app-register-to-event',
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    MatCheckboxModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    TranslocoPipe,
  ],
  templateUrl: './register-to-event.html',
})
export class RegisterToEvent {
  private readonly dialogRef = inject(MatDialogRef<RegisterToEvent, boolean>);
  private readonly dialog = inject(MatDialog);
  private readonly fb = inject(NonNullableFormBuilder);
  private readonly registrationService = inject(RegistrationService);
  private readonly notification = inject(NotificationService);

  protected readonly data = inject<RegisterToEventDialogData>(MAT_DIALOG_DATA);
  protected readonly event = this.data.event;
  protected readonly isEditMode = this.data.mode === 'edit';

  protected readonly foodPreferences: FoodPreference[] = Object.values(FoodPreferenceEnum);
  protected readonly isInternal = this.event.type === EventTypeEnum.INTERNAL;
  protected readonly isExternal = this.event.type === EventTypeEnum.EXTERNAL;
  protected readonly requiresFoodPreference = this.event.foodProvided === true;
  protected readonly registrationClosed =
    this.isEditMode && this.event.registrationEndDate < new Date().toISOString().slice(0, 10);

  protected readonly gdprExpanded = signal(false);
  protected readonly photoExpanded = signal(false);
  protected readonly saving = signal(false);
  protected readonly loading = signal(this.isEditMode);

  protected readonly registrationForm: FormGroup<RegistrationFormControls> =
    this.fb.group<RegistrationFormControls>({
      gdprConsent: this.fb.control(false, this.isExternal ? [] : [Validators.requiredTrue]),
      photoConsent: this.fb.control(false),
      foodPreference: this.fb.control<FoodPreference | null>(
        null,
        this.requiresFoodPreference ? [Validators.required] : []
      ),
      transportNeeded: this.fb.control(false),
      driverName: this.fb.control(''),
      driverPhoneNumber: this.fb.control(''),
      accommodationNeeded: this.fb.control(false),
      accommodationDays: this.fb.control(1),
    });

  constructor() {
    if (this.isInternal) {
      this.registrationForm
        .get('transportNeeded')!
        .valueChanges.pipe(takeUntilDestroyed())
        .subscribe((needed) => this.toggleDriverValidators(needed));
      this.registrationForm
        .get('accommodationNeeded')!
        .valueChanges.pipe(takeUntilDestroyed())
        .subscribe((needed) => this.toggleAccommodationValidators(needed));
    }

    if (this.isEditMode) {
      this.registrationService
        .getRegistration(this.event.id)
        .pipe(
          catchError(() => {
            this.notification.showError('event-registration.messages.load-error');
            this.dialogRef.close(false);
            return EMPTY;
          }),
          takeUntilDestroyed()
        )
        .subscribe((registration) => {
          this.applyRegistration(registration);
          this.loading.set(false);
        });
    }
  }

  private applyRegistration(registration: RegistrationResponse): void {
    this.registrationForm.patchValue({
      gdprConsent: registration.gdprConsent,
      photoConsent: registration.photoConsent,
      foodPreference: registration.foodPreference,
      transportNeeded: registration.transportNeeded ?? false,
      accommodationNeeded: registration.accommodationNeeded ?? false,
    });
    this.registrationForm.patchValue({
      driverName: registration.driverName ?? '',
      driverPhoneNumber: registration.driverPhoneNumber ?? '',
      accommodationDays: registration.accommodationDays ?? 1,
    });

    if (this.registrationClosed) {
      this.registrationForm.disable();
    }
  }

  private toggleDriverValidators(needed: boolean): void {
    const driverName = this.registrationForm.get('driverName')!;
    const driverPhoneNumber = this.registrationForm.get('driverPhoneNumber')!;

    if (needed) {
      driverName.setValidators([
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(64),
      ]);
      driverPhoneNumber.setValidators([
        Validators.required,
        Validators.pattern(/^(\+40|0)7[0-9]{8}$/),
      ]);
    } else {
      driverName.clearValidators();
      driverPhoneNumber.clearValidators();
      driverName.setValue('');
      driverPhoneNumber.setValue('');
    }

    driverName.updateValueAndValidity();
    driverPhoneNumber.updateValueAndValidity();
  }

  private toggleAccommodationValidators(needed: boolean): void {
    const accommodationDays = this.registrationForm.get('accommodationDays')!;

    if (needed) {
      accommodationDays.setValidators([Validators.required, Validators.min(1)]);
    } else {
      accommodationDays.clearValidators();
    }
    accommodationDays.setValue(1);
    accommodationDays.updateValueAndValidity();
  }

  protected incrementAccommodationDays(): void {
    const control = this.registrationForm.get('accommodationDays')!;
    control.setValue(control.value + 1);
  }

  protected decrementAccommodationDays(): void {
    const control = this.registrationForm.get('accommodationDays')!;
    control.setValue(control.value > 1 ? control.value - 1 : 1);
  }

  protected cancel(): void {
    this.dialogRef.close(false);
  }

  protected save(): void {
    if (this.registrationClosed) return;

    if (this.registrationForm.invalid) {
      this.registrationForm.markAllAsTouched();
      return;
    }

    const confirmRef = this.dialog.open<ConfirmDialog, ConfirmDialogData, boolean>(ConfirmDialog, {
      data: this.isEditMode
        ? {
            titleKey: 'event-registration.edit-dialog.title',
            messageKey: 'event-registration.edit-dialog.message',
            messageParams: { name: this.event.name },
            confirmLabelKey: 'event-registration.actions.update',
            confirmIcon: 'save',
          }
        : {
            titleKey: 'event-registration.confirm-dialog.title',
            messageKey: 'event-registration.confirm-dialog.message',
            messageParams: { name: this.event.name },
            warningKey: 'event-registration.confirm-dialog.warning',
            confirmLabelKey: 'event-registration.actions.save',
            confirmIcon: 'how_to_reg',
          },
      width: '28rem',
      autoFocus: 'dialog',
      restoreFocus: true,
    });

    confirmRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;
      this.submit();
    });
  }

  protected withdraw(): void {
    const confirmRef = this.dialog.open<ConfirmDialog, ConfirmDialogData, boolean>(ConfirmDialog, {
      data: {
        titleKey: 'event-registration.withdraw-dialog.title',
        messageKey: 'event-registration.withdraw-dialog.message',
        messageParams: { name: this.event.name },
        warningKey: 'event-registration.withdraw-dialog.warning',
        confirmLabelKey: 'event-registration.actions.withdraw',
        confirmIcon: 'person_remove',
      },
      width: '28rem',
      autoFocus: 'dialog',
      restoreFocus: true,
    });

    confirmRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;

      this.saving.set(true);
      this.registrationService
        .withdraw(this.event.id)
        .pipe(
          catchError(() => EMPTY),
          finalize(() => this.saving.set(false))
        )
        .subscribe(() => {
          this.notification.showSuccess('event-registration.messages.withdraw-success');
          this.dialogRef.close(true);
        });
    });
  }

  private submit(): void {
    this.saving.set(true);

    const request$ = this.isEditMode
      ? this.registrationService.editRegistration(this.event.id, this.buildRequest())
      : this.registrationService.register(this.event.id, this.buildRequest());

    request$
      .pipe(
        catchError(() => EMPTY),
        finalize(() => this.saving.set(false))
      )
      .subscribe(() => {
        this.notification.showSuccess(
          this.isEditMode
            ? 'event-registration.messages.update-success'
            : 'event-registration.messages.success'
        );
        this.dialogRef.close(true);
      });
  }

  private buildRequest(): RegistrationRequest {
    const value = this.registrationForm.getRawValue();
    const transportNeeded = this.isInternal ? value.transportNeeded : null;
    const accommodationNeeded = this.isInternal ? value.accommodationNeeded : null;

    return {
      gdprConsent: value.gdprConsent,
      photoConsent: value.photoConsent,
      foodPreference: this.requiresFoodPreference ? value.foodPreference : null,
      transportNeeded,
      driverName: transportNeeded ? value.driverName : null,
      driverPhoneNumber: transportNeeded ? value.driverPhoneNumber : null,
      accommodationNeeded,
      accommodationDays: accommodationNeeded ? value.accommodationDays : null,
    };
  }
}
