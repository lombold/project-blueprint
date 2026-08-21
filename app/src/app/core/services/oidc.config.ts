import { InjectionToken } from '@angular/core';

export interface OidcConfig {
  authorizationEndpoint: string;
  clientId: string;
  endSessionEndpoint: string;
  nativeRedirectUri: string;
  tokenEndpoint: string;
}

export const OIDC_CONFIG = new InjectionToken<OidcConfig>('OIDC_CONFIG');
