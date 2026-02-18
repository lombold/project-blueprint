export * from './health.service';
import { HealthService } from './health.service';
export * from './users.service';
import { UsersService } from './users.service';
export const APIS = [HealthService, UsersService];
