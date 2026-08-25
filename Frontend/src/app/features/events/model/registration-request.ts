import { FoodPreference } from '@features/events/model/food-preference';

export type RegistrationRequest = {
  gdprConsent: boolean;
  photoConsent: boolean;
  foodPreference: FoodPreference | null;
  transportNeeded: boolean | null;
  driverName: string | null;
  driverPhoneNumber: string | null;
  accommodationNeeded: boolean | null;
  accommodationDays: number | null;
};
