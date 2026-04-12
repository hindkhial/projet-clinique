import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Patient, PatientCreateRequest, PatientUpdateRequest, PatientAccountResult } from '../models/patient.model';
import { ApiResponse } from '../models/api-response.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class PatientService {
  private http = inject(HttpClient);
  private authBase = `${environment.apiUrl}/auth/patient`;
  private readBase = `${environment.apiUrl}/patient`;

  // ── READ (depuis /patient) ──────────────────────────────
  getAll(): Observable<Patient[]> {
    return this.http
      .get<ApiResponse<Patient[]>>(`${this.readBase}/`)
      .pipe(map(r => r.data!));
  }

  getById(id: number): Observable<Patient> {
    return this.http
      .get<ApiResponse<Patient>>(`${this.readBase}/${id}`)
      .pipe(map(r => r.data!));
  }

  search(q: string): Observable<Patient[]> {
    return this.http
      .get<ApiResponse<Patient[]>>(`${this.readBase}/search`, { params: { q } })
      .pipe(map(r => r.data!));
  }

  // ── WRITE (depuis /auth/patient) ────────────────────────
  create(payload: PatientCreateRequest): Observable<PatientAccountResult> {
    return this.http
      .post<ApiResponse<PatientAccountResult>>(`${this.authBase}/register`, payload)
      .pipe(map(r => r.data!));
  }

  update(id: number, payload: PatientUpdateRequest): Observable<PatientAccountResult> {
    return this.http
      .put<ApiResponse<PatientAccountResult>>(`${this.authBase}/update/${id}`, payload)
      .pipe(map(r => r.data!));
  }

  delete(id: number): Observable<void> {
    return this.http
      .delete<ApiResponse<string>>(`${this.authBase}/delete/${id}`)
      .pipe(map(() => void 0));
  }
}
