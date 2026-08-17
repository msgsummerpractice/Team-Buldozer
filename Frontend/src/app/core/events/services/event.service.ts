import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventDetails } from '@features/event-details/models/event.details.model';
import { environment } from '@environments/environment';

@Service()
export class EventService {
  private readonly http = inject(HttpClient);

  getEventById(id: number): Observable<EventDetails> {
    return this.http.get<EventDetails>(`${environment.apiUrl}/api/v1/events/${id}`);
  }
}
