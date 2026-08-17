export type EventLocation = 'CLUJ' | 'TIMISOARA' | 'MURES' | 'ALL';
export type EventType = 'INTERNAL' | 'EXTERNAL' | 'LOCAL';
export type EventStatus = 'DRAFT' | 'PUBLISHED' | 'COMPLETED';

export interface EventDetails {
  id: number;
  name: string;
  description: string;
  startDateTime: string;
  endDateTime: string;
  location: EventLocation;
  type: EventType;
  status: EventStatus;
  poster: string | null;
  registrationStartDate: string | null;
  registrationEndDate: string | null;
  address: string | null;
  createdById: number;
  foodProvided: boolean;
  createdAt: string;
}
