import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {env} from '../../env/env';
import { Observable } from 'rxjs';
import { HttpParams } from '@angular/common/http';
import { PagedResponse } from '../shared/model/paged-response';
import { CertificateResponse } from './models/certificate-response.model';
import { CreateRootCertificateRequest } from './models/create-root-certificate.model';
import {CertificateDetails} from './models/certificate-details-response.model';
import { CreateSubordinateCertificateRequest } from './models/create-subordinate-certificate.model';
import { RevocationRequest } from './models/revocation-reason.model';
import { RevocationResponse } from './models/revocation-response.model';

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
    return this.httpClient.get<PagedResponse<CertificateResponse>>(
      `${env.apiHost}/certificates`,
      { params: params }
    );
  }

  createRootCertificate(request: CreateRootCertificateRequest): Observable<void> {
    return this.httpClient.post<void>(`${env.apiHost}/certificates/root`, request);
  }

  createSubordinateCertificate(request: CreateSubordinateCertificateRequest): Observable<void> {
    return this.httpClient.post<void>(`${env.apiHost}/certificates/subordinate`, request);
  }
 
  getCertificate(id: string): Observable<CertificateDetails> {
    return this.httpClient.get<CertificateDetails>(`${env.apiHost}/certificates/${id}`);
  }

  downloadCertificate(serialNumber: string): Observable<Blob> {
    return this.httpClient.get(`${env.apiHost}/certificates/${serialNumber}/download`, {
      responseType: 'blob'
    });
  }

  getEndEntityCertificates(page: number, size: number): Observable<PagedResponse<CertificateResponse>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.httpClient.get<PagedResponse<CertificateResponse>>(
      `${env.apiHost}/certificates/end-entities/`,
      { params: params }
    );
  }

  getValidCACertificates(page: number, size: number) : Observable<PagedResponse<CertificateResponse>>{
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.httpClient.get<PagedResponse<CertificateResponse>>(`${env.apiHost}/certificates/valid-cas`, { params: params });
  }

  revokeCertificate(id: string, reason: RevocationRequest): Observable<RevocationResponse> {
    return this.httpClient.post<RevocationResponse>(`${env.apiHost}/certificates/${id}/revocation`, reason);
  }
  
}
