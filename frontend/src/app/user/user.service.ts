import { Injectable } from '@angular/core';
import { UserResponse } from './model/user-response.model';
import { PagedResponse } from '../shared/model/paged-response';
import { Observable } from 'rxjs';
import { HttpClient, HttpParams } from '@angular/common/http';
import { env } from '../../env/env';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) { }

  getAll(page: number, size: number): Observable<PagedResponse<UserResponse>> {
    const params = new HttpParams()
      .set('page', page)
      .set('size', size);
    return this.http.get<PagedResponse<UserResponse>>(`${env.apiHost}/users`, { params: params });
    }
}
