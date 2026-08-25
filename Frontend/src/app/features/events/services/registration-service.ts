import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { RegistrationRequest } from '@features/events/model/registration-request';
import { RegistrationResponse } from '@features/events/model/registration-response';

@Service()
export class RegistrationService {
  private readonly http = inject(HttpClient);
  private readonly eventsUrl = `${environment.apiUrl}/events`;

  register(eventId: number, request: RegistrationRequest): Observable<RegistrationResponse> {
    return this.http.post<RegistrationResponse>(
      `${this.eventsUrl}/${eventId}/registrations`,
      request
    );
  }
}
