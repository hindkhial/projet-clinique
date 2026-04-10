// src/app/auth/token.interceptor.ts
import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from './auth.service';

// routes that don't need a token
const PUBLIC_URLS = ['/auth/login', '/auth/refresh'];

const isPublic = (url: string): boolean =>
  PUBLIC_URLS.some(path => url.includes(path));

const addHeaders = (req: HttpRequest<unknown>, token: string, userId: string, role: string) =>
  req.clone({
    setHeaders: {
      'Authorization': `Bearer ${token}`,
      'X-User-Id':     userId,
      'X-User-Role':   role,
    }
  });

export const tokenInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  const auth = inject(AuthService);

  // ── Skip public routes ────────────────────────────────────────────────────
  if (isPublic(req.url)) {
    return next(req);
  }

  // ── Attach headers ────────────────────────────────────────────────────────
  const token  = auth.getAccessToken();
  const userId = auth.getUserId() ?? '';
  const role   = auth.getRoles()[0] ?? '';

  const authReq = token ? addHeaders(req, token, userId, role) : req;

  // ── Send + handle 401 ─────────────────────────────────────────────────────
  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {

      // not a 401 — pass the error through unchanged
      if (error.status !== 401) {
        return throwError(() => error);
      }

      // 401 — try to refresh then retry the original request
      return auth.forceRefresh().pipe(
        switchMap(newToken => {
          const newUserId = auth.getUserId() ?? '';
          const newRole   = auth.getRoles()[0] ?? '';
          const retryReq  = addHeaders(req, newToken, newUserId, newRole);
          return next(retryReq);
        }),
        catchError(refreshError => {
          // refresh failed — logout and stop
          auth.logout();
          return throwError(() => refreshError);
        })
      );
    })
  );
};