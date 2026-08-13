import { Routes } from '@angular/router';
import { Home } from '@features/home/components/home';
import { NotFound } from '@features/not-found/components/not-found';
import { authorizationGuard } from '@core/authorization/guards/authorization.guard';

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
    path: 'login',
    loadComponent: () => import('@features/login/components/login').then((m) => m.Login),
  },
  {
    path: 'register',
    loadComponent: () => import('@features/register/components/register').then((m) => m.Register),
  },
  {
    path: 'users',
    loadComponent: () => import('@features/users/users').then((m) => m.Users),
    canActivate: [authorizationGuard(['ADMIN'])],
  },
  { path: '404', component: NotFound },
  { path: '**', redirectTo: '404' },
];
