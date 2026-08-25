import { FoodPreference } from '@features/events/model/food-preference';

export type RegistrationResponse = {
  id: number;
  eventId: number;
  userId: number;
  registrationDate: string;
  gdprConsent: boolean;
  photoConsent: boolean;
  foodPreference: FoodPreference | null;
  transportNeeded: boolean | null;
  driverName: string | null;
  driverPhoneNumber: string | null;
  accommodationNeeded: boolean | null;
  accommodationDays: number | null;
};
