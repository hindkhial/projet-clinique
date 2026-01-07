import {inject, Injectable} from '@angular/core';
import {PatientModel} from '../models/patient.model';
import {ApiResponse} from '../models/apiResponse.model'
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PatientService {
  private http = inject(HttpClient)
  url = "http://localhost:9090/patient"

  getPatients(): Observable<ApiResponse<PatientModel[]>> {
    return this.http.get<ApiResponse<PatientModel[]>>('http://localhost:9090/patient/');
  }

  addPatient(patient: PatientModel): Observable<ApiResponse<PatientModel>> {
    return this.http.post<ApiResponse<PatientModel>>(`${this.url}/create`, patient);
  }

  getPatientById(id: number): Observable<ApiResponse<PatientModel>> {
    return this.http.get<ApiResponse<PatientModel>>(`${this.url}/${id}`);
  }

  updatePatient(id: number, patient: PatientModel): Observable<ApiResponse<PatientModel>> {
    return this.http.put<ApiResponse<PatientModel>>(`${this.url}/update/${id}`, patient);
  }

  deletePatient(id: number): Observable<ApiResponse<string>> {
    return this.http.delete<ApiResponse<string>>(`${this.url}/delete/${id}`);
  }
}
