import { EventLocation } from '@features/events/model/event-location';
import { EventType } from '@features/events/model/event-type';

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
