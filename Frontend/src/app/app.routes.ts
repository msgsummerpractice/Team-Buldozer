import { Routes } from '@angular/router';
import { Home } from '@features//home/components/home';
import { NotFound } from '@features/not-found/components/not-found';
import {Users} from '@features/users/users';

export const routes: Routes = [
  { path: '', component: Home, pathMatch: 'full' },
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
    component: Users
  },
  { path: '404', component: NotFound },
  { path: '**', redirectTo: '404' },
];
