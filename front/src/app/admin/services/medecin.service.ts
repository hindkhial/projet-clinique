import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  MedecinResponseDto,
  MedecinRegistrationRequest,
  MedecinUpdateRequestAuth,
  MedecinAccountResult,
} from '../models/medecin.models';
import { ApiResponse } from '../../shared/models/api-response.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class MedecinService {
  private http = inject(HttpClient);
  private readBase  = `${environment.apiUrl}/medecin`;
  private authBase  = `${environment.apiUrl}/auth/medecin`;

  // ── READ ────────────────────────────────────────────────
  getAll(): Observable<MedecinResponseDto[]> {
    return this.http
      .get<ApiResponse<MedecinResponseDto[]>>(`${this.readBase}/`)
      .pipe(map(r => r.data!));
  }

  getById(id: number): Observable<MedecinResponseDto> {
    return this.http
      .get<ApiResponse<MedecinResponseDto>>(`${this.readBase}/${id}`)
      .pipe(map(r => r.data!));
  }

  // ── WRITE ───────────────────────────────────────────────
  create(dto: MedecinRegistrationRequest): Observable<MedecinAccountResult> {
    return this.http
      .post<ApiResponse<MedecinAccountResult>>(`${this.authBase}/register`, dto)
      .pipe(map(r => r.data!));
  }

  update(id: number, dto: MedecinUpdateRequestAuth): Observable<MedecinAccountResult> {
    return this.http
      .put<ApiResponse<MedecinAccountResult>>(`${this.authBase}/update/${id}`, dto)
      .pipe(map(r => r.data!));
  }

  delete(id: number): Observable<void> {
    return this.http
      .delete<ApiResponse<string>>(`${this.authBase}/delete/${id}`)
      .pipe(map(() => void 0));
  }
}
