import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {PagedResponse} from '../shared/model/paged-response';
import {CertificateResponse} from './models/certificate-response.model';
import {env} from '../../env/env';

@Injectable({
  providedIn: 'root'
})
export class CertificateService {

  constructor(private httpClient: HttpClient) { }

  getAll(page: number, size: number): Observable<PagedResponse<CertificateResponse>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.httpClient.get<PagedResponse<CertificateResponse>>(`${env.apiHost}/certificates`)
  }

}
