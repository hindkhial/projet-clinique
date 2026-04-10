// src/app/auth/auth.service.ts
import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap, map } from 'rxjs';
import { TokenResponse, LoginResponse, LoginRequest } from '../shared/models/auth.model';
import { environment } from '../../environments/environment';

const ACCESS_TOKEN_KEY  = 'access_token';
const REFRESH_TOKEN_KEY = 'refresh_token';

const REDIRECT_MAP: Record<string, string> = {
  PATIENT:        '/patient/dashboard',
  RECEPTIONNISTE: '/receptionniste/dashboard',
  TECHNICIEN:     '/technicien/radios/upload',
  RADIOLOGUE:     '/radiologue/radios',
  MEDECIN:        '/medecin/dashboard',
};

@Injectable({ providedIn: 'root' })
export class AuthService {

  private http   = inject(HttpClient);
  private router = inject(Router);

  // ── Signals ───────────────────────────────────────────────────────────────
  private _accessToken  = signal<string | null>(localStorage.getItem(ACCESS_TOKEN_KEY));
  private _refreshToken = signal<string | null>(localStorage.getItem(REFRESH_TOKEN_KEY));

  private _payload = computed(() => this._decodePayload(this._accessToken()));

  private _refreshTimer: ReturnType<typeof setTimeout> | null = null;

  // ── Public API ────────────────────────────────────────────────────────────

  isLoggedIn(): boolean {
    const payload = this._payload();
    if (!payload) return false;
    return payload['exp'] * 1000 > Date.now();
  }

  getUserId(): string | null {
    return this._payload()?.['sub'] ?? null;
  }

  getRoles(): string[] {
    const payload = this._payload();
    if (!payload) return [];
    return payload['realm_access']?.['roles'] ?? [];
}

  getAccessToken(): string | null {
    return this._accessToken();
}

  hasRole(role: string): boolean {
    return this.getRoles().includes(role);
  }

  getEmail(): string | null {
    return this._payload()?.['email'] ?? null;
  }

  getInternalId(): number | null {
    return this._payload()?.['internalId'] ?? null;
  }

  // ── Login / Logout ────────────────────────────────────────────────────────

  login(credentials: LoginRequest): Observable<LoginResponse> {
  return this.http
    .post<LoginResponse>(`${environment.apiUrl}/auth/login`, credentials)
    .pipe(
      tap(response => {
        const { accessToken, refreshToken } = response.data;
        this._storeTokens(accessToken, refreshToken);
        this._scheduleRefresh();

        const roles = this.getRoles();
        const role  = roles.find(r => REDIRECT_MAP[r]);
        const destination = role ? REDIRECT_MAP[role] : '/login';
        this.router.navigate([destination]);
      })
    );
}

  logout(): void {
    this._clearRefreshTimer();
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(REFRESH_TOKEN_KEY);
    this._accessToken.set(null);
    this._refreshToken.set(null);
    this.router.navigate(['/login']);
  }

  // ── Token refresh ─────────────────────────────────────────────────────────

  forceRefresh(): Observable<string> {
    const refreshToken = this._refreshToken();

    if (!refreshToken) {
      this.logout();
      throw new Error('No refresh token available');
    }

    return this.http
      .post<TokenResponse>('/api/auth/refresh', { refreshToken })
      .pipe(
        tap({
          next: response => {
            this._storeTokens(response.accessToken, response.refreshToken);
            this._scheduleRefresh();
          },
          error: () => this.logout()
        }),
        // return only the new access token string
        // the interceptor just needs the token, not the full response
        map(response => response.accessToken)
      );
  }

  // ── Helpers ───────────────────────────────────────────────────────────────

  private _scheduleRefresh(): void {
    this._clearRefreshTimer();

    const payload = this._payload();
    if (!payload?.['exp']) return;

    const expiresAt  = payload['exp'] * 1000;       // ms
    const refreshAt  = expiresAt - 60_000;           // 60s before expiry
    const delay      = refreshAt - Date.now();

    if (delay <= 0) {
      // token already close to expiry or expired — refresh immediately
      this.forceRefresh().subscribe();
      return;
    }

    this._refreshTimer = setTimeout(() => {
      this.forceRefresh().subscribe();
    }, delay);
  }

  private _clearRefreshTimer(): void {
    if (this._refreshTimer) {
      clearTimeout(this._refreshTimer);
      this._refreshTimer = null;
    }
  }

  private _storeTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
    localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
    this._accessToken.set(accessToken);
    this._refreshToken.set(refreshToken);
  }

  private _decodePayload(token: string | null): Record<string, any> | null {
    if (!token) return null;
    try {
      const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
      return JSON.parse(atob(base64));
    } catch {
      return null;
    }
  }
}
