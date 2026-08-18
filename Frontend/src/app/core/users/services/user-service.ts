import { inject, Service } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserResponse } from '@core/users/dto/user.response';
import { UserRole } from '@core/users/model/user-role';
import { environment } from '@environments/environment';

@Service()
export class UserService {
  private readonly http = inject(HttpClient);

  getAllUsers(): Observable<UserResponse[]> {
    return this.http.get<UserResponse[]>(`${environment.apiUrl}/users`);
  }

  updateUserStatusAndRoles(id: number, status: boolean, roles: UserRole[]): Observable<UserResponse> {
    return this.http.patch<UserResponse>(`${environment.apiUrl}/users/${id}/status-roles`, { status, roles });
  }
}
