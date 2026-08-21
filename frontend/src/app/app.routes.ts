import { Routes } from '@angular/router';
import { UsersPage } from '@pages/users/users.page';
import { LoginPage } from '@pages/login/login.page';

export interface RouteData {
  title?: string;
  subtitle?: string;
}

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full',
  },
  {
    path: 'login',
    component: LoginPage,
    data: {
      title: 'Sign in',
    },
  },
  {
    path: 'users',
    component: UsersPage,
    data: {
      title: 'Users',
    },
  },
];
