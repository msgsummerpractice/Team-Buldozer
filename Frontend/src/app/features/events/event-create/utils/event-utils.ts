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

export const toDateTimeString = (value: Date | null): string => {
  if (!value) {
    return '';
  }

  const pad = (n: number) => n.toString().padStart(2, '0');

  return (
    `${value.getFullYear()}-${pad(value.getMonth() + 1)}-${pad(value.getDate())}` +
    `T${pad(value.getHours())}:${pad(value.getMinutes())}`
  );
};

export const toDateString = (value: Date | null): string => {
  if (!value) {
    return '';
  }

  const pad = (n: number) => n.toString().padStart(2, '0');

  return `${value.getFullYear()}-${pad(value.getMonth() + 1)}-${pad(value.getDate())}`;
};

/**
 * Parses an ISO-like date string into a local `Date`.
 *
 * Accepts either a plain date (`"YYYY-MM-DD"`) or a date-time
 * (`"YYYY-MM-DDTHH:mm[:ss...]"`). The date components are always interpreted in
 * the local timezone (unlike `new Date(string)`, which treats a bare
 * `YYYY-MM-DD` as UTC) so the resulting `Date` reflects the wall-clock day the
 * user actually picked.
 *
 * @param value      The string to parse. `null`, `undefined` or empty strings
 *                   short-circuit to `null`.
 * @param includeTime When `true`, the `HH:mm` portion after `T` is applied to
 *                   the returned `Date`; missing pieces default to `0`. When
 *                   `false` (default), the time part is ignored and the `Date`
 *                   is anchored at local midnight — useful for date-only fields
 *                   (e.g. registration windows) where the time component is
 *                   irrelevant.
 * @returns A local `Date`, or `null` if the input is empty or the date part
 *          cannot be parsed into a valid year/month/day triple.
 */
export const parseDate = (
  value: string | null | undefined,
  includeTime = false
): Date | null => {
  if (!value) {
    return null;
  }

  const [datePart, timePart = '00:00'] = value.split('T');
  const [y, m, d] = datePart.split('-').map(Number);

  if (!y || !m || !d) {
    return null;
  }

  if (!includeTime) {
    return new Date(y, m - 1, d);
  }

  const [hh, mm] = timePart.split(':').map(Number);
  return new Date(y, m - 1, d, hh || 0, mm || 0, 0, 0);
};
