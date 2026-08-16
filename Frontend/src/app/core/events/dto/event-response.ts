import { EventLocation } from '@core/events/model/event-location';
import { EventType } from '@core/events/model/event-type';
import { EventStatus } from '@core/events/model/event-status';

export type EventResponse = {
  id: number;
  name: string;
  location: EventLocation;
  startDateTime: string;
  endDateTime: string;
  type: EventType;
  status: EventStatus;
  poster?: string;
  registrationStartDate: string;
  registrationEndDate: string;
  address: string;
  description: string;
  createdById?: number;
  foodProvided: boolean | null;
  createdAt: string;
};
