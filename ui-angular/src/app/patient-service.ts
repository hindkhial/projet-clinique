import {inject, Injectable} from '@angular/core';
import {PatientModel} from './models/patient.model';
import {ApiResponse} from './models/apiResponse.model'
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class PatientService {
  private http = inject(HttpClient)
  url = "http://localhost:9090/patient/"

  getPatients(): Observable<ApiResponse<PatientModel[]>> {
    return this.http.get<ApiResponse<PatientModel[]>>('http://localhost:9090/patient/');
  }


}
