import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CreateRootCertificateRequest } from './models/CreateRootCertificate.model';
import { env } from '../../env/env';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CertificateService {
  constructor(private http: HttpClient) {}

  createRootCertificate(request: CreateRootCertificateRequest): Observable<void> {
    return this.http.post<void>(`${env.apiHost}/certificates/root`, request);
  }
}
