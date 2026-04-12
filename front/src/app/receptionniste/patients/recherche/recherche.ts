import { Component, inject, resource, signal } from '@angular/core';
import { Patient } from '../../../shared/models/patient.model';
import { PatientService } from '../../../shared/services/patient.service';
import { firstValueFrom } from 'rxjs';
import { RouterLink } from '@angular/router';
import { EditModal } from './edit-modal/edit-modal';
import { DeleteModal } from './delete-modal/delete-modal';

@Component({
  selector: 'app-recherche',
  imports: [RouterLink, EditModal, DeleteModal],
  templateUrl: './recherche.html',
  standalone: true
})
export class Recherche {
  private patientService = inject(PatientService);

  searchTerm = signal('');
  editingPatient = signal<Patient | null>(null);
  deletingPatient = signal<Patient | null>(null);

  patientsResource = resource<Patient[], string>({
    params: (): string => this.searchTerm(),
    loader: (loaderParams) => {
      const q = loaderParams.params;
      return q.length >= 2
        ? firstValueFrom(this.patientService.search(q))
        : firstValueFrom(this.patientService.getAll());
    }
  });
}
