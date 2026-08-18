import { authGuard } from '@core/authentication/guards/login-guard';
import { authorizationGuard } from '@core/authorization/guards/authorization.guard';
import { UserRoleEnum } from '@core/users/model/user-role';
import { Routes } from '@angular/router';
import { draftGuard } from '@features/events/guards/draft-guard';

export const eventsRoutes: Routes = [
  {
    path: 'events',
    canActivate: [authGuard, authorizationGuard([UserRoleEnum.MARKETING])],
    children: [
      {
        path: 'list',
        loadComponent: () => import('@features/events/events').then((m) => m.Events),
      },
      {
        path: 'add',
        loadComponent: () =>
          import('@features/events/event-create/event-create').then((m) => m.EventCreate),
      },
      {
        path: ':id',
        children: [
          {
            path: 'edit',
            loadComponent: () =>
              import('@features/events/event-create/event-create').then((m) => m.EventCreate),
            canActivate: [draftGuard],
          },
          {
            path: 'details',
            redirectTo: '/events/list',
          },
          {
            path: '**',
            redirectTo: '/events/list',
          },
        ],
      },
      {
        path: '**',
        redirectTo: 'list',
      },
    ],
  },
];
