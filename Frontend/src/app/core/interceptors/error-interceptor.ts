import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { NotificationService } from '@core/notification/services/notification.service';
import { catchError, throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage: string = 'error-interceptor.default';

      if (error.error?.code) {
        errorMessage = `server-error-codes.${error.error.code}`;
      } else {
        if (error.status === 0) {
          errorMessage = 'error-interceptor.0';
        } else if (error.status === 400) {
          errorMessage = 'error-interceptor.400';
        } else if (error.status === 401) {
          errorMessage = 'error-interceptor.401';
        } else if (error.status === 403) {
          errorMessage = 'error-interceptor.403';
        } else if (error.status >= 500) {
          errorMessage = 'error-interceptor.500';
        }
      }

      notificationService.showError(errorMessage);

      return throwError(() => error);
    })
  );
};
