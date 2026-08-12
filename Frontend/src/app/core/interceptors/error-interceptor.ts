<<<<<<< HEAD
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
=======
import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { NotificationService } from '@core/services/notification-service';
import { catchError, throwError } from 'rxjs';
import { TranslocoService } from '@jsverse/transloco';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);
  const translocoService = inject(TranslocoService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = translocoService.translate('error-interceptor.default');

      if (error.status === 0) {
        errorMessage = translocoService.translate('error-interceptor.0');
      } else if (error.status === 401) {
        errorMessage = translocoService.translate('error-interceptor.401');
      } else if (error.status === 403) {
        errorMessage = translocoService.translate('error-interceptor.403');
      } else if (error.status >= 500) {
        errorMessage = translocoService.translate('error-interceptor.500');
>>>>>>> 1f6264579d13a196a1672d81aa8736fb6197dcfd
      }

      notificationService.showError(errorMessage);

      return throwError(() => error);
    })
<<<<<<< HEAD
  )

}
=======
  );
};
>>>>>>> 1f6264579d13a196a1672d81aa8736fb6197dcfd
