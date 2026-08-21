import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
  provideZonelessChangeDetection,
} from '@angular/core';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import {
  RouteReuseStrategy,
  provideRouter,
  withPreloading,
  PreloadAllModules,
} from '@angular/router';
import { Configuration } from '@core/api';
import { authSessionInterceptor } from '@core/services/auth-session.interceptor';
import { AuthService } from '@core/services/auth.service';
import { OIDC_CONFIG } from '@core/services/oidc.config';
import { IonicRouteStrategy, provideIonicAngular } from '@ionic/angular/standalone';
import { environment } from '../environments/environment';
import { routes } from './app.routes';

export function apiConfigurationFactory(authService: AuthService): Configuration {
  return new Configuration({
    basePath: environment.apiBaseUrl,
    credentials: { bearerAuth: () => authService.accessToken() },
  });
}

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideAppInitializer(() => inject(AuthService).initialize()),
    provideZonelessChangeDetection(),
    { provide: RouteReuseStrategy, useClass: IonicRouteStrategy },
    provideIonicAngular(),
    provideRouter(routes, withPreloading(PreloadAllModules)),
    provideHttpClient(withInterceptors([authSessionInterceptor])),
    { provide: OIDC_CONFIG, useValue: environment.oidc },
    {
      provide: Configuration,
      useFactory: apiConfigurationFactory,
      deps: [AuthService],
    },
  ],
};
