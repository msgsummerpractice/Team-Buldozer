import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '@environments/environment';
import { CheckInResponse } from '@features/events/check-in/model/check-in-response';

@Service()
export class AttendanceService {
  private readonly http = inject(HttpClient);
  private readonly checkInsUrl = `${environment.apiUrl}/attendance/check-ins`;

  checkInByCode(checkInCode: string): Observable<CheckInResponse> {
    return this.http.post<CheckInResponse>(this.checkInsUrl, { checkInCode });
  }

  checkInByQrCode(eventId: number, eventName: string): Observable<CheckInResponse> {
    return this.http.post<CheckInResponse>(`${this.checkInsUrl}/qr-code`, { eventId, eventName });
  }
}
