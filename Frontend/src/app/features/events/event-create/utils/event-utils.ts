import { AbstractControl, ValidationErrors } from '@angular/forms';

export const combineDateAndTime = (date: Date | null, time: Date | null): Date | null => {
  if (!date) {
    return null;
  }

  const merged = new Date(date);
  if (time) {
    merged.setHours(time.getHours(), time.getMinutes(), 0, 0);
  } else {
    merged.setHours(0, 0, 0, 0);
  }

  return merged;
};

export const futureEventStartValidator = (control: AbstractControl): ValidationErrors | null => {
  const parent = control.parent;
  if (!parent) {
    return null;
  }

  const startDate = parent.get('startDate')?.value as Date | null;
  const startTime = parent.get('startTime')?.value as Date | null;

  if (!startDate || !startTime) {
    return null;
  }

  const start = combineDateAndTime(startDate, startTime);

  if (start && start <= new Date()) {
    return { eventStartInPast: true };
  }

  return null;
};

export const futureRegistrationStartValidator = (
  control: AbstractControl
): ValidationErrors | null => {
  const value = control.value as Date | null;
  if (!value) {
    return null;
  }

  const today = new Date();
  today.setHours(0, 0, 0, 0);

  if (new Date(value) < today) {
    return { registrationStartInPast: true };
  }

  return null;
};

export const eventDateRangeValidator = (control: AbstractControl): ValidationErrors | null => {
  const parent = control.parent;
  if (!parent) {
    return null;
  }

  const startDate = parent.get('startDate')?.value as Date | null;
  const startTime = parent.get('startTime')?.value as Date | null;
  const endDate = parent.get('endDate')?.value as Date | null;
  const endTime = parent.get('endTime')?.value as Date | null;

  if (!startDate || !startTime || !endDate || !endTime) {
    return null;
  }

  const start = combineDateAndTime(startDate, startTime);
  const end = combineDateAndTime(endDate, endTime);

  if (start && end && end <= start) {
    return { invalidEventDates: true };
  }

  return null;
};

export const registrationDateRangeValidator = (
  control: AbstractControl
): ValidationErrors | null => {
  const start = control.parent?.get('registrationStartDate')?.value;
  const end = control.parent?.get('registrationEndDate')?.value;

  if (start && end && new Date(end) < new Date(start)) {
    return { invalidRegistrationDates: true };
  }

  return null;
};
