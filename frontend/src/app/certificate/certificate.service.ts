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
import { CaCertificate } from './models/ca-certificate.model';

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

  getAuthorizedIssuableCertificates(page: number, size: number): Observable<PagedResponse<CertificateResponse>> {
  const params = new HttpParams()
    .set('page', page)
    .set('size', size);
    return this.httpClient.get<PagedResponse<CertificateResponse>>(`${env.apiHost}/certificates/valid-authorized-cas`, { params });  
  }

  getAvailableCaCertificates(): Observable<CaCertificate[]> {
    return this.httpClient.get<CaCertificate[]>(`${env.apiHost}/certificates/available-ca`);
  }

  createCSRSelfGenerate(caCertificateId: string, validTo: string, pemFile: File): Observable<void> {
      const formData = new FormData();
      formData.append('caCertificateId', caCertificateId);
      formData.append('validTo', validTo);
      formData.append('pemFile', pemFile);

      return this.httpClient.post<void>(
        `${env.apiHost}/certificates/csr/self-generation`, 
        formData
      );
  }
}
