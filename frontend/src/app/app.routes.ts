import { Routes } from '@angular/router';
import { UsersPage } from '@pages/users/users.page';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/users',
    pathMatch: 'full'
  },
  {
    path: 'users',
    component: UsersPage
  },
];
