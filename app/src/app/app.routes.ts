import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'users',
    title: 'Users',
    loadComponent: () => import('./pages/users/users.page').then((module) => module.UsersPage),
  },
  {
    path: '',
    redirectTo: 'users',
    pathMatch: 'full',
  },
  { path: '**', redirectTo: 'users' },
];
