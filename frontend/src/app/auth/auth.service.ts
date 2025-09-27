import { Injectable } from '@angular/core';
import {BehaviorSubject, finalize, Observable, shareReplay, tap, throwError} from 'rxjs';
import { RegisterRequest } from './model/register-request.model';
import { HttpClient } from '@angular/common/http';
import { env } from '../../env/env';
import {LoginRequest} from './model/login-request.model';
import {LoginResponse} from './model/login-response.model';
import {jwtDecode} from 'jwt-decode';
import {JwtPayload} from './model/jwt-payload.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private accessToken$ = new BehaviorSubject<string | null>(null);
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private refreshInProgress$: Observable<LoginResponse> | null = null

  constructor(private http: HttpClient) { }

  register(request: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${env.apiHost}/users/registration`, request);
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${env.apiHost}/auth/login`, request);
  }

  refresh(): Observable<LoginResponse> {
    if (this.refreshInProgress$) {
      return this.refreshInProgress$;
    }

    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token'));
    }

    this.refreshInProgress$ = this.http.post<LoginResponse>(`${env.apiHost}/auth/refresh`, { token: refreshToken }).pipe(
      tap(res => this.setTokens(res)),
      shareReplay(1),
      finalize(() => this.refreshInProgress$ = null)
    );

    return this.refreshInProgress$;
  }

  setTokens(response: LoginResponse): void {
    localStorage.setItem(this.REFRESH_TOKEN_KEY, response.refreshToken);
    this.accessToken$.next(response.accessToken);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  getAccessToken(): string | null {
    return this.accessToken$.value;
  }

  tryRestoreSession(): Observable<LoginResponse> | null {
    const refresh = this.getRefreshToken();
    if (!refresh) return null;
    return this.refresh();
  }

  clearTokens(): void {
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    this.accessToken$.next(null);
  }

  getRole(): string | null {
    const token = this.getAccessToken();
    if (!token) return null;

    try {
      const decoded = jwtDecode<JwtPayload>(token);
      return decoded.role ?? null;
    } catch (e) {
      return null;
    }
  }
}
