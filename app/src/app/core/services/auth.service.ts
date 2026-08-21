import { DOCUMENT } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { App as CapacitorApp } from '@capacitor/app';
import { Browser } from '@capacitor/browser';
import { Capacitor } from '@capacitor/core';
import { firstValueFrom } from 'rxjs';
import { OIDC_CONFIG } from './oidc.config';

const oidcStateKey = 'oidc_state';
const oidcCodeVerifierKey = 'oidc_code_verifier';
const oidcResponseParams = ['code', 'state', 'session_state', 'iss', 'error', 'error_description'];

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly document = inject(DOCUMENT);
  private readonly http = inject(HttpClient);
  private readonly oidcConfig = inject(OIDC_CONFIG);
  private readonly router = inject(Router);
  private readonly profile = signal<UserProfile | undefined>(this.readStoredProfile());
  private nativeListenerRegistered = false;

  readonly displayName = computed(
    () => this.profile()?.name ?? this.profile()?.preferred_username ?? 'Account',
  );

  async initialize(): Promise<void> {
    if (!Capacitor.isNativePlatform()) {
      await this.handleLoginRedirect(this.document.location.href);
      return;
    }
    if (!this.nativeListenerRegistered) {
      this.nativeListenerRegistered = true;
      await CapacitorApp.addListener('appUrlOpen', ({ url }) => {
        void this.handleNativeCallback(url);
      });
    }
    const launchUrl = await CapacitorApp.getLaunchUrl();
    if (launchUrl?.url) {
      await this.handleNativeCallback(launchUrl.url);
    }
  }

  async login(): Promise<void> {
    const state = this.randomString();
    const codeVerifier = this.randomString();
    const codeChallenge = await this.codeChallenge(codeVerifier);
    this.view()?.sessionStorage.setItem(oidcStateKey, state);
    this.view()?.sessionStorage.setItem(oidcCodeVerifierKey, codeVerifier);
    const url = this.loginUrl(this.redirectUri(), { codeChallenge, state });
    if (Capacitor.isNativePlatform()) {
      await Browser.open({ url });
      return;
    }
    this.document.location.assign(url);
  }

  async logout(): Promise<void> {
    const idToken =
      this.view()?.localStorage.getItem('id_token') ??
      this.view()?.sessionStorage.getItem('id_token') ??
      undefined;
    this.clearStoredSession();
    const redirectUri = Capacitor.isNativePlatform()
      ? this.oidcConfig.nativeRedirectUri
      : new URL('login', this.document.baseURI).toString();
    const url = this.logoutUrl(redirectUri, idToken);
    if (Capacitor.isNativePlatform()) {
      await Browser.open({ url });
      return;
    }
    this.document.location.assign(url);
  }

  loginUrl(redirectUri: string, options: LoginOptions = {}): string {
    const url = new URL(this.oidcConfig.authorizationEndpoint);
    url.searchParams.set('client_id', this.oidcConfig.clientId);
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

  logoutUrl(redirectUri: string, idToken?: string): string {
    const url = new URL(this.oidcConfig.endSessionEndpoint);
    url.searchParams.set('client_id', this.oidcConfig.clientId);
    url.searchParams.set('post_logout_redirect_uri', redirectUri);
    if (idToken) {
      url.searchParams.set('id_token_hint', idToken);
    }
    return url.toString();
  }

  accessToken(): string | undefined {
    const token = this.view()?.localStorage.getItem('access_token') ?? undefined;
    return token && !this.isExpired(token) ? token : undefined;
  }

  async handleAuthenticationFailure(): Promise<void> {
    this.clearStoredSession();
    await this.login();
  }

  async handleLoginRedirect(url: string): Promise<void> {
    const currentUrl = new URL(url);
    const code = currentUrl.searchParams.get('code');
    if (!code) {
      if (Capacitor.isNativePlatform()) {
        await this.router.navigateByUrl('/login', { replaceUrl: true });
      } else {
        this.removeOidcResponseParams(currentUrl);
      }
      return;
    }

    const state = currentUrl.searchParams.get('state');
    const expectedState = this.view()?.sessionStorage.getItem(oidcStateKey);
    const codeVerifier = this.view()?.sessionStorage.getItem(oidcCodeVerifierKey);
    if (!expectedState || state !== expectedState || !codeVerifier) {
      throw new Error('Invalid OIDC login response.');
    }

    const redirectUri = Capacitor.isNativePlatform()
      ? this.oidcConfig.nativeRedirectUri
      : new URL(currentUrl.pathname, currentUrl.origin).toString();
    const body = new URLSearchParams({
      client_id: this.oidcConfig.clientId,
      code,
      code_verifier: codeVerifier,
      grant_type: 'authorization_code',
      redirect_uri: redirectUri,
    }).toString();
    const response = await firstValueFrom(
      this.http.post<TokenResponse>(
        this.oidcConfig.tokenEndpoint,
        body,
        { headers: new HttpHeaders({ 'Content-Type': 'application/x-www-form-urlencoded' }) },
      ),
    );

    this.storeTokens(response);
    this.view()?.sessionStorage.removeItem(oidcStateKey);
    this.view()?.sessionStorage.removeItem(oidcCodeVerifierKey);
    if (Capacitor.isNativePlatform()) {
      await this.router.navigateByUrl('/users', { replaceUrl: true });
    } else {
      this.removeOidcResponseParams(currentUrl);
    }
  }

  private async handleNativeCallback(url: string): Promise<void> {
    await Browser.close();
    await this.handleLoginRedirect(url);
  }

  private redirectUri(): string {
    return Capacitor.isNativePlatform()
      ? this.oidcConfig.nativeRedirectUri
      : new URL('users', this.document.baseURI).toString();
  }

  private removeOidcResponseParams(url: URL): void {
    const hasOidcParams = oidcResponseParams.some((param) => url.searchParams.has(param));
    if (!hasOidcParams) {
      return;
    }
    for (const param of oidcResponseParams) {
      url.searchParams.delete(param);
    }
    const view = this.view();
    view?.history.replaceState(view.history.state, '', url.pathname + url.search + url.hash);
  }

  private clearStoredSession(): void {
    for (const key of ['access_token', 'id_token', 'refresh_token']) {
      this.view()?.localStorage.removeItem(key);
      this.view()?.sessionStorage.removeItem(key);
    }
    this.view()?.sessionStorage.removeItem(oidcStateKey);
    this.view()?.sessionStorage.removeItem(oidcCodeVerifierKey);
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
