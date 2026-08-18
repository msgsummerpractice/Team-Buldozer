import { Routes } from '@angular/router';
import { Home } from '@features/home/components/home';
import { NotFound } from '@features/not-found/components/not-found';
import { authorizationGuard } from '@core/authorization/guards/authorization.guard';
import { ownProfileGuard } from '@core/authentication/guards/own-profile.guard';
import { UserRoleEnum } from '@core/users/model/user-role';
import { Users } from '@features/users/components/users';
import { authGuard } from '@core/authentication/guards/login-guard';

export const routes: Routes = [
  {
    path: '',
    component: Home,
    pathMatch: 'full',
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
    path: 'users',
    loadComponent: () => import('@features/users/components/users').then((m) => m.Users),
    canActivate: [authorizationGuard([UserRoleEnum.ADMIN])],
  },
  {
    path: 'events/add',
    loadComponent: () => import('@features/event-create/event-create').then((m) => m.EventCreate),
    canActivate: [authGuard, authorizationGuard([UserRoleEnum.MARKETING])],
  },
  {
    path: 'events',
    loadComponent: () => import('@features/events/events').then((m) => m.Events),
    canActivate: [authGuard],
  },
  {
    path: 'users',
    loadComponent: () => import('@features/users/users').then((m) => m.Users),
    canActivate: [authorizationGuard([UserRoleEnum.ADMIN])],
  },
  { path: '404', component: NotFound },
  { path: '**', redirectTo: '404' },
];
