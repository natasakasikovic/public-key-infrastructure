import { Injectable } from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import { RegisterRequest } from './model/register-request.model';
import { HttpClient } from '@angular/common/http';
import { env } from '../../env/env';
import {LoginRequest} from './model/login-request.model';
import {LoginResponse} from './model/login-response.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private accessToken$ = new BehaviorSubject<string | null>(null);
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';

  constructor(private http: HttpClient) { }

  register(request: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${env.apiHost}/users/registration`, request);
  }

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${env.apiHost}/auth/login`, request);
  }

  refresh(): Observable<LoginResponse> {
    const refreshToken = this.getRefreshToken();
    return this.http.post<LoginResponse>(`${env.apiHost}/auth/refresh`, { refreshToken })
  }

  setTokens(response: LoginResponse): void {
    sessionStorage.setItem(this.REFRESH_TOKEN_KEY, response.refreshToken);
    this.accessToken$.next(response.accessToken);
  }

  getRefreshToken(): string | null {
    return sessionStorage.getItem(this.REFRESH_TOKEN_KEY);
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
    sessionStorage.removeItem(this.REFRESH_TOKEN_KEY);
    this.accessToken$.next(null);
  }
}
