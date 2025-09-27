import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {env} from '../../env/env';
import { Observable } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { PagedResponse } from '../shared/model/paged-response';
import { CertificateResponse } from './models/certificate-response.model';
import { CreateRootCertificateRequest } from './models/CreateRootCertificate.model';

@Injectable({
  providedIn: 'root',
})
export class CertificateService {

  constructor(private httpClient: HttpClient) {
  }

  getAll(page: number, size: number): Observable<PagedResponse<CertificateResponse>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.httpClient.get<PagedResponse<CertificateResponse>>(`${env.apiHost}/certificates`);
  }

  createRootCertificate(request: CreateRootCertificateRequest): Observable<void> {
    return this.httpClient.post<void>(`${env.apiHost}/certificates/root`, request);
  }

}
