import { DOCUMENT } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth.service';
import { OIDC_CONFIG, type OidcConfig } from './oidc.config';

describe('AuthService', () => {
  const oidcConfig: OidcConfig = {
    authorizationEndpoint: 'https://identity.example.test/oauth2/authorize',
    clientId: 'mobile-client',
    endSessionEndpoint: 'https://identity.example.test/oauth2/logout',
    nativeRedirectUri: 'com.example.projectname://auth/callback',
    tokenEndpoint: 'https://identity.example.test/oauth2/token',
  };
  let http: HttpTestingController;
  let locationAssign: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    localStorage.clear();
    sessionStorage.clear();
    locationAssign = vi.fn();
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([]),
        { provide: OIDC_CONFIG, useValue: oidcConfig },
        {
          provide: DOCUMENT,
          useValue: {
            baseURI: 'http://localhost:4200/',
            defaultView: window,
            location: {
              assign: locationAssign,
              href: 'http://localhost:4200/users',
            },
          } as unknown as Document,
        },
      ],
    });
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('builds the native OIDC authorization URL from configuration', () => {
    const service = TestBed.inject(AuthService);
    const url = new URL(
      service.loginUrl('com.example.projectname://auth/callback', {
        codeChallenge: 'challenge',
      }),
    );

    expect(url.origin + url.pathname).toBe('https://identity.example.test/oauth2/authorize');
    expect(url.searchParams.get('client_id')).toBe('mobile-client');
    expect(url.searchParams.get('redirect_uri')).toBe('com.example.projectname://auth/callback');
    expect(url.searchParams.get('code_challenge')).toBe('challenge');
    expect(url.searchParams.get('code_challenge_method')).toBe('S256');
  });

  it('reads the display name from a stored identity token', () => {
    localStorage.setItem(
      'id_token',
      ['header', btoa(JSON.stringify({ name: 'Default User' })), 'signature'].join('.'),
    );

    const service = TestBed.inject(AuthService);

    expect(service.displayName()).toBe('Default User');
  });

  it('clears the local session and uses the configured OIDC end-session endpoint', async () => {
    localStorage.setItem('access_token', 'access-token');
    localStorage.setItem('id_token', 'id-token');
    localStorage.setItem('refresh_token', 'refresh-token');
    sessionStorage.setItem('oidc_state', 'state');
    sessionStorage.setItem('oidc_code_verifier', 'verifier');
    const service = TestBed.inject(AuthService);

    await service.logout();

    expect(localStorage.getItem('access_token')).toBeNull();
    expect(localStorage.getItem('id_token')).toBeNull();
    expect(localStorage.getItem('refresh_token')).toBeNull();
    expect(sessionStorage.getItem('oidc_state')).toBeNull();
    expect(sessionStorage.getItem('oidc_code_verifier')).toBeNull();
    const logoutUrl = new URL(locationAssign.mock.calls[0]?.[0] as string);
    expect(logoutUrl.origin + logoutUrl.pathname).toBe(
      'https://identity.example.test/oauth2/logout',
    );
    expect(logoutUrl.searchParams.get('id_token_hint')).toBe('id-token');
    expect(logoutUrl.searchParams.get('post_logout_redirect_uri')).toBe(
      'http://localhost:4200/login',
    );
  });

  it('exchanges a browser authorization callback for tokens', async () => {
    sessionStorage.setItem('oidc_state', 'expected-state');
    sessionStorage.setItem('oidc_code_verifier', 'code-verifier');
    const service = TestBed.inject(AuthService);

    const exchange = service.handleLoginRedirect(
      'http://localhost:4200/users?code=auth-code&state=expected-state',
    );
    const request = http.expectOne('https://identity.example.test/oauth2/token');
    expect(request.request.body).toContain('grant_type=authorization_code');
    expect(request.request.body).toContain('code_verifier=code-verifier');
    request.flush({ access_token: 'access-token' });
    await exchange;

    expect(service.accessToken()).toBe('access-token');
  });
});
