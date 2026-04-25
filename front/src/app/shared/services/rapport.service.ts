import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RapportResponse, CreateRapportRequest } from '../models/rapport.models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class RapportService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/rapport`;

  getById(id: number): Observable<RapportResponse> {
    return this.http.get<RapportResponse>(`${this.base}/${id}`);
  }

  getByMedecin(id: number): Observable<RapportResponse[]> {
    return this.http.get<RapportResponse[]>(`${this.base}/medecin/${id}`);
  }

  getByPatient(id: number): Observable<RapportResponse[]> {
    return this.http.get<RapportResponse[]>(`${this.base}/patient/${id}`);
  }

  create(dto: CreateRapportRequest): Observable<RapportResponse> {
    return this.http.post<RapportResponse>(`${this.base}/create`, dto);
  }

  generatePdf(id: number): Observable<RapportResponse> {
    return this.http.post<RapportResponse>(`${this.base}/${id}/generatepdf`, {});
  }

  download(id: number): Observable<Blob> {
    return this.http.get(`${this.base}/${id}/download`, { responseType: 'blob' });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
