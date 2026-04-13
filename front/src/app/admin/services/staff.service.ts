import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  StaffResponseDto,
  StaffRegistrationRequest,
  StaffUpdateRequestAuth,
  StaffAccountResult,
} from '../models/staff.models';
import { ApiResponse } from '../../shared/models/api-response.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class StaffService {
  private http = inject(HttpClient);
  private readBase = `${environment.apiUrl}/staff`;
  private authBase = `${environment.apiUrl}/auth/staff`;

  // ── READ ────────────────────────────────────────────────
  getAll(): Observable<StaffResponseDto[]> {
    return this.http
      .get<ApiResponse<StaffResponseDto[]>>(`${this.readBase}/`)
      .pipe(map(r => r.data!));
  }

  getById(id: number): Observable<StaffResponseDto> {
    return this.http
      .get<ApiResponse<StaffResponseDto>>(`${this.readBase}/${id}`)
      .pipe(map(r => r.data!));
  }

  // ── WRITE ───────────────────────────────────────────────
  create(dto: StaffRegistrationRequest): Observable<StaffAccountResult> {
    return this.http
      .post<ApiResponse<StaffAccountResult>>(`${this.authBase}/register`, dto)
      .pipe(map(r => r.data!));
  }

  update(id: number, dto: StaffUpdateRequestAuth): Observable<StaffAccountResult> {
    return this.http
      .put<ApiResponse<StaffAccountResult>>(`${this.authBase}/update/${id}`, dto)
      .pipe(map(r => r.data!));
  }

  delete(id: number): Observable<void> {
    return this.http
      .delete<ApiResponse<string>>(`${this.authBase}/delete/${id}`)
      .pipe(map(() => void 0));
  }
}
