import { Routes } from '@angular/router';
import { UsersPage } from '@pages/users/users.page';

export interface RouteData {
  title?: string;
  subtitle?: string;
}

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/users',
    pathMatch: 'full'
  },
  {
    path: 'users',
    component: UsersPage,
    data: {
      title: 'Users',
    }
  },
];
