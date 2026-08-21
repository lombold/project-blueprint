import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import { Configuration } from '@core/api';
import { authSessionInterceptor } from '@core/services/auth-session.interceptor';
import { AuthService } from '@core/services/auth.service';

export function apiConfigurationFactory(authService: AuthService): Configuration {
  return new Configuration({
    basePath: '',
    credentials: { bearerAuth: () => authService.accessToken() },
  });
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideAppInitializer(() => inject(AuthService).handleLoginRedirect()),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authSessionInterceptor])),
    {
      provide: Configuration,
      useFactory: apiConfigurationFactory,
      deps: [AuthService],
    },
  ],
};
