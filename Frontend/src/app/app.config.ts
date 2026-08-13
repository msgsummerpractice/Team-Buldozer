import { ApplicationConfig, isDevMode } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authenticationInterceptor } from '@core/authentication/interceptors/authentication.interceptor';
import { provideServiceWorker } from '@angular/service-worker';
import { TranslocoHttpLoader } from '@core/i18n/services/transloco-loader';
import { provideTransloco } from '@jsverse/transloco';
import { errorInterceptor } from '@core/interceptors/error-interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideHttpClient(withInterceptors([authenticationInterceptor, errorInterceptor])),
    provideServiceWorker('ngsw-worker.js', {
      enabled: !isDevMode(),
      registrationStrategy: 'registerWhenStable:30000',
    }),
    provideTransloco({
      config: {
        availableLangs: ['en', 'ro'],
        defaultLang: 'en',
        fallbackLang: 'ro',
        missingHandler: {
          allowEmpty: false,
          useFallbackTranslation: true,
        },
        reRenderOnLangChange: true,
        prodMode: !isDevMode(),
      },
      loader: TranslocoHttpLoader,
    }),
  ],
};
