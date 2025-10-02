import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {CertificateTemplate} from './model/certificate-template.model';
import {Observable} from 'rxjs';
import {env} from '../../env/env';

@Injectable({
  providedIn: 'root'
})
export class TemplateService {

  constructor(private httpClient: HttpClient) { }

  createTemplate(request: CertificateTemplate): Observable<CertificateTemplate> {
    return this.httpClient.post<CertificateTemplate>(`${env.apiHost}/templates`, request);
  }

  getTemplate(id: string): Observable<CertificateTemplate> {
    return this.httpClient.get<CertificateTemplate>(`${env.apiHost}/templates/${id}`);
  }

  getTemplates(): Observable<CertificateTemplate[]> {
    return this.httpClient.get<CertificateTemplate[]>(`${env.apiHost}/templates`);
  }

  getTemplatesByIssuer(issuer: string): Observable<CertificateTemplate[]> {
    return this.httpClient.get<CertificateTemplate[]>(`${env.apiHost}/templates/issuer/${issuer}`);
  }

  updateTemplate(id: string, request: CertificateTemplate): Observable<CertificateTemplate> {
    return this.httpClient.put<CertificateTemplate>(`${env.apiHost}/templates/${id}`, request);
  }

  deleteTemplate(id: string): Observable<void> {
    return this.httpClient.delete<void>(`${env.apiHost}/templates/${id}`);
  }
}
