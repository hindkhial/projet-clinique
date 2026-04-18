import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RadioResponse, RadioValidationRequest } from '../models/radio.models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class RadioService {
  private http = inject(HttpClient);
  private base = `${environment.apiUrl}/Radio`;

  getAll(): Observable<RadioResponse[]> {
    return this.http.get<RadioResponse[]>(`${this.base}/`);
  }

  getById(id: number): Observable<RadioResponse> {
    return this.http.get<RadioResponse>(`${this.base}/${id}`);
  }

  getByPatient(patientId: number): Observable<RadioResponse[]> {
    return this.http.get<RadioResponse[]>(`${this.base}/Patient/${patientId}`);
  }

  getByRadiologue(id: number): Observable<RadioResponse[]> {
    return this.http.get<RadioResponse[]>(`${this.base}/Radiologue/${id}`);
  }

  getPredicted(id: number): Observable<RadioResponse[]> {
    return this.http.get<RadioResponse[]>(`${this.base}/Radiologue/${id}/predicted`);
  }

  validate(id: number, req: RadioValidationRequest): Observable<RadioResponse> {
    return this.http.post<RadioResponse>(`${this.base}/validate/${id}`, req);
  }

  upload(formData: FormData): Observable<RadioResponse> {
    return this.http.post<RadioResponse>(`${this.base}/Upload`, formData);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }

  download(id: number): Observable<Blob> {
    return this.http.get(`${this.base}/${id}/download`, { responseType: 'blob' });
  }

  downloadHeatmap(id: number): Observable<Blob> {
    return this.http.get(`${this.base}/${id}/heatmap/download`, { responseType: 'blob' });
  }
}
