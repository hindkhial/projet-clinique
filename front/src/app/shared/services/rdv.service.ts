import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { ApiResponse } from '../models/api-response.model';
import { RdvCreateRequest, RdvUpdateRequest, RdvResponse } from '../models/rdv.models';
import {environment} from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class RdvService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/rdv`;
  // private base = `${environment.apiUrl}/rdv`;


  getAll(): Observable<RdvResponse[]> {
    return this.http
      .get<ApiResponse<RdvResponse[]>>(`${this.base}/`)
      .pipe(map(r => r.data ?? []));
  }

  getDisponibilites(idMedecin: number, date: string): Observable<string[]> {
    // date attendu : yyyy-MM-dd
    return this.http
      .get<ApiResponse<string[]>>(`${this.base}/disponibilites/${idMedecin}`, {
        params: { date }
      })
      .pipe(map(r => r.data ?? []));
  }

  create(dto: RdvCreateRequest): Observable<ApiResponse<RdvResponse>> {
    return this.http.post<ApiResponse<RdvResponse>>(`${this.base}/create`, dto);
  }

  update(id: number, dto: RdvUpdateRequest): Observable<ApiResponse<RdvResponse>> {
    return this.http.put<ApiResponse<RdvResponse>>(`${this.base}/update/${id}`, dto);
  }

  delete(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.base}/delete/${id}`);
  }
}
