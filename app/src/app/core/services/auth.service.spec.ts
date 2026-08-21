import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let http: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    sessionStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])],
    });
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('builds the native Keycloak login URL', () => {
    const service = TestBed.inject(AuthService);
    const url = new URL(
      service.loginUrl('com.example.projectname://auth/callback', {
        codeChallenge: 'challenge',
      }),
    );

    expect(url.origin + url.pathname).toBe(
      'http://localhost:8082/realms/project-name/protocol/openid-connect/auth',
    );
    expect(url.searchParams.get('client_id')).toBe('project-name-app');
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

  it('exchanges a browser authorization callback for tokens', async () => {
    sessionStorage.setItem('oidc_state', 'expected-state');
    sessionStorage.setItem('oidc_code_verifier', 'code-verifier');
    const service = TestBed.inject(AuthService);

    const exchange = service.handleLoginRedirect(
      'http://localhost:4200/users?code=auth-code&state=expected-state',
    );
    const request = http.expectOne(
      'http://localhost:8082/realms/project-name/protocol/openid-connect/token',
    );
    expect(request.request.body).toContain('grant_type=authorization_code');
    expect(request.request.body).toContain('code_verifier=code-verifier');
    request.flush({ access_token: 'access-token' });
    await exchange;

    expect(service.accessToken()).toBe('access-token');
  });
});
