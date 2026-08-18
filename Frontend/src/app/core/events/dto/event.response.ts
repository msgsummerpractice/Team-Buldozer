import { EventLocation } from '@core/events/model/event-location';
import { EventStatus } from '@core/events/model/event-status';
import { EventType } from '@core/events/model/event-type';

export type EventResponse = {
  id: number;
  name: string;
  location: EventLocation;
  startDateTime: string;
  endDateTime: string;
  type: EventType;
  status: EventStatus;
  poster: string | null;
  registrationStartDate: string;
  registrationEndDate: string;
  address: string;
  description: string;
  createdById: number | null;
  foodProvided: boolean | null;
  createdAt: string;
};
