import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { ConsultationResponse, ConsultationCreateRequest, ConsultationUpdateRequest } from '../models/consultation.models';
import { ApiResponse } from '../models/api-response.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ConsultationService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/consultation`;

  getAll(): Observable<ConsultationResponse[]> {
    return this.http
      .get<ApiResponse<ConsultationResponse[]>>(`${this.base}/`)
      .pipe(map(r => r.data!));
  }

  getById(id: number): Observable<ConsultationResponse> {
    return this.http
      .get<ApiResponse<ConsultationResponse>>(`${this.base}/${id}`)
      .pipe(map(r => r.data!));
  }

  getByMedecin(idMedecin: number): Observable<ConsultationResponse[]> {
    return this.http
      .get<ApiResponse<ConsultationResponse[]>>(`${this.base}/`)
      .pipe(map(r => (r.data ?? []).filter(c => c.idMedecin === idMedecin)));
  }

  create(dto: ConsultationCreateRequest): Observable<ConsultationResponse> {
    return this.http
      .post<ApiResponse<ConsultationResponse>>(`${this.base}/create`, dto)
      .pipe(map(r => r.data!));
  }

  update(id: number, dto: ConsultationUpdateRequest): Observable<ConsultationResponse> {
    return this.http
      .put<ApiResponse<ConsultationResponse>>(`${this.base}/update/${id}`, dto)
      .pipe(map(r => r.data!));
  }

  delete(id: number): Observable<void> {
    return this.http
      .delete<ApiResponse<string>>(`${this.base}/delete/${id}`)
      .pipe(map(() => void 0));
  }
}
