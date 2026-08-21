import { DOCUMENT } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { firstValueFrom } from 'rxjs';

const issuer = 'http://localhost:8082/realms/project-name';
const clientId = 'project-name-frontend';
const oidcStateKey = 'oidc_state';
const oidcCodeVerifierKey = 'oidc_code_verifier';
const oidcReturnPathKey = 'oidc_return_path';
const oidcResponseParams = ['code', 'state', 'session_state', 'iss', 'error', 'error_description'];

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly document = inject(DOCUMENT);
  private readonly http = inject(HttpClient);
  private readonly profile = signal<UserProfile | undefined>(this.readStoredProfile());

  readonly displayName = computed(
    () => this.profile()?.name ?? this.profile()?.preferred_username ?? 'Account',
  );

  async login(): Promise<void> {
    const state = this.randomString();
    const codeVerifier = this.randomString();
    const codeChallenge = await this.codeChallenge(codeVerifier);
    this.view()?.sessionStorage.setItem(oidcStateKey, state);
    this.view()?.sessionStorage.setItem(oidcCodeVerifierKey, codeVerifier);
    this.document.location.assign(
      this.loginUrl(this.authenticatedRedirectUrl(), { codeChallenge, state }),
    );
  }

  logout(): void {
    this.clearStoredSession();
    this.document.location.assign(this.logoutUrl(this.loginRedirectUrl()));
  }

  loginUrl(redirectUri: string, options: LoginOptions = {}): string {
    const url = new URL(`${issuer}/protocol/openid-connect/auth`);
    url.searchParams.set('client_id', clientId);
    url.searchParams.set('redirect_uri', redirectUri);
    url.searchParams.set('response_type', 'code');
    url.searchParams.set('scope', 'openid profile email');
    if (options.state) {
      url.searchParams.set('state', options.state);
    }
    if (options.codeChallenge) {
      url.searchParams.set('code_challenge', options.codeChallenge);
      url.searchParams.set('code_challenge_method', 'S256');
    }
    return url.toString();
  }

  logoutUrl(redirectUri: string): string {
    const url = new URL(`${issuer}/protocol/openid-connect/logout`);
    url.searchParams.set('client_id', clientId);
    url.searchParams.set('post_logout_redirect_uri', redirectUri);
    return url.toString();
  }

  accessToken(): string | undefined {
    const token = this.view()?.localStorage.getItem('access_token') ?? undefined;
    return token && !this.isExpired(token) ? token : undefined;
  }

  async handleAuthenticationFailure(): Promise<void> {
    this.rememberReturnPath();
    this.clearStoredSession({ preserveReturnPath: true });
    await this.login();
  }

  async handleLoginRedirect(): Promise<void> {
    const currentUrl = new URL(this.document.location.href);
    const code = currentUrl.searchParams.get('code');
    if (!code) {
      this.removeOidcResponseParams(currentUrl);
      return;
    }

    const state = currentUrl.searchParams.get('state');
    const expectedState = this.view()?.sessionStorage.getItem(oidcStateKey);
    const codeVerifier = this.view()?.sessionStorage.getItem(oidcCodeVerifierKey);
    if (!expectedState || state !== expectedState || !codeVerifier) {
      throw new Error('Invalid OIDC login response.');
    }

    const redirectUri = new URL(currentUrl.pathname, currentUrl.origin).toString();
    const body = new URLSearchParams({
      client_id: clientId,
      code,
      code_verifier: codeVerifier,
      grant_type: 'authorization_code',
      redirect_uri: redirectUri,
    }).toString();
    const response = await firstValueFrom(
      this.http.post<TokenResponse>(`${issuer}/protocol/openid-connect/token`, body, {
        headers: new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' }),
      }),
    );

    this.storeTokens(response);
    this.view()?.sessionStorage.removeItem(oidcStateKey);
    this.view()?.sessionStorage.removeItem(oidcCodeVerifierKey);
    if (!this.restoreReturnPath(currentUrl)) {
      this.removeOidcResponseParams(currentUrl);
    }
  }

  private rememberReturnPath(): void {
    const currentUrl = new URL(this.document.location.href);
    if (currentUrl.pathname.endsWith('/login')) {
      return;
    }
    this.view()?.sessionStorage.setItem(
      oidcReturnPathKey,
      currentUrl.pathname + currentUrl.search + currentUrl.hash,
    );
  }

  private restoreReturnPath(currentUrl: URL): boolean {
    const view = this.view();
    const returnPath = view?.sessionStorage.getItem(oidcReturnPathKey);
    view?.sessionStorage.removeItem(oidcReturnPathKey);
    if (!returnPath) {
      return false;
    }
    const target = new URL(returnPath, currentUrl.origin);
    if (target.origin !== currentUrl.origin) {
      return false;
    }
    this.removeOidcResponseParams(target, true);
    return true;
  }

  private removeOidcResponseParams(url: URL, replaceUnchanged = false): void {
    const hasOidcParams = oidcResponseParams.some((param) => url.searchParams.has(param));
    if (!replaceUnchanged && !hasOidcParams) {
      return;
    }
    for (const param of oidcResponseParams) {
      url.searchParams.delete(param);
    }
    const view = this.view();
    view?.history.replaceState(view.history.state, '', url.pathname + url.search + url.hash);
  }

  private authenticatedRedirectUrl(): string {
    return new URL('users', this.document.baseURI).toString();
  }

  private loginRedirectUrl(): string {
    return new URL('login', this.document.baseURI).toString();
  }

  private clearStoredSession(options: { preserveReturnPath?: boolean } = {}): void {
    for (const key of ['access_token', 'id_token', 'refresh_token']) {
      this.view()?.localStorage.removeItem(key);
      this.view()?.sessionStorage.removeItem(key);
    }
    if (!options.preserveReturnPath) {
      this.view()?.sessionStorage.removeItem(oidcReturnPathKey);
    }
    this.profile.set(undefined);
  }

  private storeTokens(response: TokenResponse): void {
    this.view()?.localStorage.setItem('access_token', response.access_token);
    if (response.id_token) {
      this.view()?.localStorage.setItem('id_token', response.id_token);
    }
    if (response.refresh_token) {
      this.view()?.localStorage.setItem('refresh_token', response.refresh_token);
    }
    this.profile.set(this.readStoredProfile());
  }

  private readStoredProfile(): UserProfile | undefined {
    const idToken = this.view()?.localStorage.getItem('id_token');
    if (!idToken) {
      return undefined;
    }
    try {
      return JSON.parse(atob(this.toBase64(idToken.split('.')[1] ?? ''))) as UserProfile;
    } catch {
      return undefined;
    }
  }

  private isExpired(token: string): boolean {
    try {
      const payload = JSON.parse(atob(this.toBase64(token.split('.')[1] ?? ''))) as {
        exp?: number;
      };
      return payload.exp !== undefined && payload.exp * 1000 <= Date.now();
    } catch {
      return false;
    }
  }

  private toBase64(value: string): string {
    const normalized = value.replaceAll('-', '+').replaceAll('_', '/');
    return normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '=');
  }

  private randomString(): string {
    const bytes = new Uint8Array(32);
    this.view()?.crypto.getRandomValues(bytes);
    return this.base64Url(bytes);
  }

  private async codeChallenge(codeVerifier: string): Promise<string> {
    const digest = await this.view()?.crypto.subtle.digest(
      'SHA-256',
      new TextEncoder().encode(codeVerifier),
    );
    if (!digest) {
      throw new Error('Web Crypto is not available.');
    }
    return this.base64Url(new Uint8Array(digest));
  }

  private base64Url(bytes: Uint8Array): string {
    return btoa(String.fromCharCode(...bytes))
      .replaceAll('+', '-')
      .replaceAll('/', '_')
      .replaceAll('=', '');
  }

  private view(): Window | null {
    return this.document.defaultView;
  }
}

interface LoginOptions {
  codeChallenge?: string;
  state?: string;
}

interface UserProfile {
  name?: string;
  preferred_username?: string;
}

interface TokenResponse {
  access_token: string;
  id_token?: string;
  refresh_token?: string;
}
