import { ApplicationConfig, isDevMode } from '@angular/core';
import { provideRouter, withViewTransitions } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';
import { routes } from './app.routes';
import { authenticationInterceptor } from '@core/authentication/interceptors/authentication.interceptor';
import { provideServiceWorker } from '@angular/service-worker';
import { TranslocoHttpLoader } from '@core/i18n/services/transloco-loader';
import { provideTransloco } from '@jsverse/transloco';
import { errorInterceptor } from '@core/interceptors/error-interceptor';
import { loadingInterceptor } from '@core/interceptors/loading-interceptor';
import { MatPaginatorIntl } from '@angular/material/paginator';
import { TranslocoPaginatorIntl } from '@core/i18n/services/transloco-paginator-intl';
import { provideNativeDateAdapter } from '@angular/material/core';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes, withViewTransitions()),
    provideHttpClient(
      withInterceptors([authenticationInterceptor, errorInterceptor, loadingInterceptor])
    ),
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
    {
      provide: MatPaginatorIntl,
      useClass: TranslocoPaginatorIntl,
    },
    provideNativeDateAdapter(),
  ],
};
