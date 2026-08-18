import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventResponse } from '@core/events/model/event-response';
import { environment } from '@environments/environment';
import { EventRequest } from '@core/events/model/event-request';

@Service()
export class EventService {
  private readonly http = inject(HttpClient);

  getAllEvents(): Observable<EventResponse[]> {
    return this.http.get<EventResponse[]>(`${environment.apiUrl}/events`);
  }

  addEvent(request: EventRequest): Observable<EventResponse> {
    return this.http.post<EventResponse>(`${environment.apiUrl}/events`, request);
  }
}
