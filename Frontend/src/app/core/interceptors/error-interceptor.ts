import {HttpErrorResponse, HttpInterceptorFn} from '@angular/common/http';
import {inject} from '@angular/core';
import {NotificationService} from '@core/services/notification-service';
import {catchError, throwError} from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {

  const notificationService = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = "An unexpected error occurred";

      if (error.status === 0) {
        errorMessage = 'No connection returned';
      } else if (error.status === 401) {
        errorMessage = 'Session expired. Please try again later.';
      } else if (error.status === 403) {
        errorMessage = 'You do not have permission to access this page.';
      } else if (error.status >= 500) {
        errorMessage = 'Internal Server Error';
      } else if (error.error?.message) {
        errorMessage = error.error.message;
      }

      notificationService.showError(errorMessage);

      return throwError(() => error);
    })
  )

}
