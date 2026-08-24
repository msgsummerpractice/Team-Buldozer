import { Routes } from '@angular/router';
import { Home } from '@features/home/components/home';
import { NotFound } from '@features/not-found/components/not-found';
import { authorizationGuard } from '@core/authorization/guards/authorization.guard';
import { ownProfileGuard } from '@core/authentication/guards/own-profile.guard';
import { UserRoleEnum } from '@core/users/model/user-role';
import { authGuard } from '@core/authentication/guards/login-guard';
import { eventsRoutes } from '@features/events/events.routes';
import { resetPasswordGuard } from '@features/password-reset/guards/reset-password.guard';

export const routes: Routes = [
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'home',
  },
  {
    path: 'home',
    component: Home,
  },
  {
    path: 'info/:id',
    loadComponent: () => import('@features/user-profile/components/profile').then((m) => m.Profile),
    canActivate: [ownProfileGuard],
  },
  {
    path: 'login',
    loadComponent: () => import('@features/login/components/login').then((m) => m.Login),
  },
  {
    path: 'register',
    loadComponent: () => import('@features/register/components/register').then((m) => m.Register),
  },
  {
    path: 'forgot-password',
    loadComponent: () =>
      import('@features/password-reset/components/forgot-password').then((m) => m.ForgotPassword),
  },
  {
    path: 'reset-password',
    loadComponent: () =>
      import('@features/password-reset/components/reset-password').then((m) => m.ResetPassword),
    canActivate: [resetPasswordGuard],
  },
  {
    path: 'users',
    loadComponent: () => import('@features/users/components/users').then((m) => m.Users),
    canActivate: [authorizationGuard([UserRoleEnum.ADMIN])],
  },
  ...eventsRoutes,
  { path: '404', component: NotFound },
  { path: '**', redirectTo: '404' },
];
