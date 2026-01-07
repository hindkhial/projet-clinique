import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { PatientService } from '../patient-service';
import { PatientModel } from '../../models/patient.model';
import { Observable, map } from 'rxjs';

@Component({
  selector: 'app-patients-page',
  imports: [CommonModule, RouterModule],
  templateUrl: './patients-page.html',
  styleUrl: './patients-page.css',
})
export class PatientsPage implements OnInit {
  listePatients$!: Observable<PatientModel[]>;
  deleteError = '';
  deletingPatientId: number | null = null;

  constructor(private _patientService: PatientService,
              private cdr: ChangeDetectorRef) {}

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.listePatients$ = this._patientService.getPatients().pipe(
      map(res => res.data || [])
    );
  }

  onDelete(patient: PatientModel): void {
    const confirmed = confirm(
      `Êtes-vous sûr de vouloir supprimer le patient ${patient.prenom} ${patient.nom} ?`
    );

    if (confirmed) {
      this.deletingPatientId = patient.id;
      this.deleteError = '';

      this._patientService.deletePatient(patient.id).subscribe({
        next: () => {
          this.deletingPatientId = null;
          // Recharger la liste après suppression
          this.loadPatients();
          this.cdr.detectChanges(); // Forcer la détection

        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          this.deleteError = `Impossible de supprimer le patient ${patient.prenom} ${patient.nom}`;
          this.deletingPatientId = null;
        }
      });
    }
  }
}
