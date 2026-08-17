import { EventLocation } from '@core/events/model/event-location';
import { EventType } from '@core/events/model/event-type';

export type EventRequest = {
  name: string;
  startDateTime: string;
  endDateTime: string;
  type: EventType;
  registrationStartDate: string;
  registrationEndDate: string;
  address: string;
  description: string;
  foodProvided?: boolean;
  location?: EventLocation;
  poster?: string;
};
