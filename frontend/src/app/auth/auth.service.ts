import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RegisterRequest } from './model/register-request.model';
import { HttpClient } from '@angular/common/http';
import { env } from '../../env/env';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  register(request: RegisterRequest): Observable<void> {
    return this.http.post<void>(`${env.apiHost}/users/registration`, request);
  }
}
