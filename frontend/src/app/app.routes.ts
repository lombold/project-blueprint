import { Routes } from '@angular/router';
import { DashboardPage } from './pages/dashboard/dashboard.page';
import { UsersPage } from './pages/users/users.page';
import { WorkoutsPage } from './pages/workouts/workouts.page';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  },
  {
    path: 'dashboard',
    component: DashboardPage
  },
  {
    path: 'users',
    component: UsersPage
  },
  {
    path: 'workouts',
    component: WorkoutsPage
  }
];
