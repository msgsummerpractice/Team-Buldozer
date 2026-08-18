import { authGuard } from '@core/authentication/guards/login-guard';
import { authorizationGuard } from '@core/authorization/guards/authorization.guard';
import { UserRoleEnum } from '@core/users/model/user-role';
import { Routes } from '@angular/router';

export const eventsRoutes: Routes = [
  {
    path: 'events',
    canActivate: [authGuard, authorizationGuard([UserRoleEnum.MARKETING])],
    children: [
      {
        path: 'add',
        loadComponent: () =>
          import('@features/events/event-create/event-create').then((m) => m.EventCreate),
      },
      {
        path: ':id',
        loadComponent: () =>
          import('@features/events/event-create/event-create').then((m) => m.EventCreate),
      },
    ],
  },
];
