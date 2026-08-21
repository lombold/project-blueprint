import { DOCUMENT } from '@angular/common';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let http: HttpTestingController;
  let locationAssign: ReturnType<typeof vi.fn>;

  beforeEach(() => {
    localStorage.clear();
    sessionStorage.clear();
    window.history.replaceState(null, '', '/');
    locationAssign = vi.fn();

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        {
          provide: DOCUMENT,
          useValue: {
            ...document,
            baseURI: `${window.location.origin}/`,
            defaultView: window,
            location: {
              assign: locationAssign,
              get href() {
                return window.location.href;
              },
              get origin() {
                return window.location.origin;
              },
            },
          },
        },
      ],
    });

    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('builds the ProjectName Keycloak login URL', () => {
    const service = TestBed.inject(AuthService);
    const url = new URL(service.loginUrl('http://localhost:4200/users'));

    expect(url.origin + url.pathname).toBe(
      'http://localhost:8082/realms/project-name/protocol/openid-connect/auth',
    );
    expect(url.searchParams.get('client_id')).toBe('project-name-frontend');
    expect(url.searchParams.get('redirect_uri')).toBe('http://localhost:4200/users');
    expect(url.searchParams.get('response_type')).toBe('code');
    expect(url.searchParams.get('scope')).toBe('openid profile email');
  });

  it('starts an authorization-code login with PKCE', async () => {
    const service = TestBed.inject(AuthService);

    await service.login();

    expect(sessionStorage.getItem('oidc_state')).toBeTruthy();
    expect(sessionStorage.getItem('oidc_code_verifier')).toBeTruthy();
    const url = new URL(locationAssign.mock.calls[0][0]);
    expect(url.searchParams.get('code_challenge')).toBeTruthy();
    expect(url.searchParams.get('code_challenge_method')).toBe('S256');
  });

  it('exchanges the authorization code and exposes the signed-in user', async () => {
    sessionStorage.setItem('oidc_state', 'expected-state');
    sessionStorage.setItem('oidc_code_verifier', 'code-verifier');
    window.history.replaceState(null, '', '/users?code=auth-code&state=expected-state');
    const idToken = jwt({ name: 'Default User', preferred_username: 'user' });
    const service = TestBed.inject(AuthService);

    const exchange = service.handleLoginRedirect();
    const request = http.expectOne(
      'http://localhost:8082/realms/project-name/protocol/openid-connect/token',
    );
    request.flush({ access_token: 'access-token', id_token: idToken });
    await exchange;

    expect(service.accessToken()).toBe('access-token');
    expect(service.displayName()).toBe('Default User');
    expect(window.location.search).toBe('');
  });
});

function jwt(payload: object): string {
  return ['header', btoa(JSON.stringify(payload)), 'signature'].join('.');
}
