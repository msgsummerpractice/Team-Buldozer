import { Routes } from '@angular/router';
import { Home } from '@features/home/components/home';
import { NotFound } from '@features/not-found/components/not-found';
import { ParticipantHome } from '@features/home/components/participant-home';
import { MarketingHome } from '@features/home/components/marketing-home';
import { HRHome } from '@features/home/components/hr-home';
import { AdminHome } from '@features/home/components/admin-home';
import { roleGuard } from '@core/authentication/guards/role.guard';

export const routes: Routes = [
  {
    path: '',
    component: Home,
    pathMatch: 'full',
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
    path: 'home/participant',
    component: ParticipantHome,
    canActivate: [roleGuard(['participant'])],
  },
  {
    path: 'home/marketing',
    component: MarketingHome,
    canActivate: [roleGuard(['marketing'])],
  },
  {
    path: 'home/hr',
    component: HRHome,
    canActivate: [roleGuard(['hr'])],
  },
  {
    path: 'home/admin',
    component: AdminHome,
    canActivate: [roleGuard(['admin'])],
  },
  {
    path: '404',
    component: NotFound,
  },
  { path: '**', redirectTo: '404' },
];
