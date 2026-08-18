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
  registrationStartDate: string;
  registrationEndDate: string;
  address: string;
  description: string;
  foodProvided: boolean | null;
  createdAt: string;
  poster?: string;
  createdById?: number;
};
