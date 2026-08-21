import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventResponse } from '@features/events/model/event-response';
import { environment } from '@environments/environment';
import { EventRequest } from '@features/events/model/event-request';
import { EventUpdateRequest } from '@features/events/model/event-update-request';
import { CreateEventResponse } from '@features/events/model/create-event-response';
import { EventCodesResponse } from '@features/events/model/event-codes-response';

@Service()
export class EventService {
  private readonly http = inject(HttpClient);
  private readonly eventsUrl = `${environment.apiUrl}/events`;

  addEvent(request: EventRequest): Observable<CreateEventResponse> {
    return this.http.post<CreateEventResponse>(this.eventsUrl, request);
  }

  getEventById(id: number): Observable<EventResponse> {
    return this.http.get<EventResponse>(`${this.eventsUrl}/${id}`);
  }

  updateEvent(id: number, request: EventUpdateRequest): Observable<EventResponse> {
    return this.http.patch<EventResponse>(`${this.eventsUrl}/${id}`, request);
  }

  completeEvent(id: number): Observable<EventResponse> {
    return this.http.patch<EventResponse>(`${this.eventsUrl}/${id}/complete`, null);
  }

  checkInEvent(eventId: number, code: string): Observable<void> {
    return this.http.post<void>(`${this.eventsUrl}/${eventId}/checkin`, { code });
  }

  getAllEvents(): Observable<EventResponse[]> {
    return this.http.get<EventResponse[]>(this.eventsUrl);
  }

  publishEvent(id: number): Observable<EventResponse> {
    return this.http.patch<EventResponse>(`${this.eventsUrl}/${id}/publish`, null);
  }

  generateCodes(id: number): Observable<EventCodesResponse> {
    return this.http.post<EventCodesResponse>(`${this.eventsUrl}/${id}/codes`, null);
  }

  getEventCodes(id: number): Observable<EventCodesResponse> {
    return this.http.get<EventCodesResponse>(`${this.eventsUrl}/${id}/codes`);
  }
}
