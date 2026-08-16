import { EventLocation } from '@core/events/model/event-location';
import { EventType } from '@core/events/model/event-type';

export type EventRequest = {
  name: string;
  location?: EventLocation;
  startDateTime: string;
  endDateTime: string;
  type: EventType;
  poster?: string | null;
  registrationStartDate: string;
  registrationEndDate: string;
  address: string;
  description: string;
  foodProvided?: boolean | null;
};
