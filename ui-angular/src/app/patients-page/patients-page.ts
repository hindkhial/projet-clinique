import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PatientService } from '../patient-service';
import { PatientModel } from '../models/patient.model';
import { Observable, map } from 'rxjs';

@Component({
  selector: 'app-patients-page',
  imports: [CommonModule],
  templateUrl: './patients-page.html',
  styleUrl: './patients-page.css',
})
export class PatientsPage implements OnInit {
  listePatients$!: Observable<PatientModel[]>;

  constructor(private _patientService: PatientService) {}

  ngOnInit(): void {
    this.listePatients$ = this._patientService.getPatients().pipe(
      map(res => res.data || [])
    );
  }
}
