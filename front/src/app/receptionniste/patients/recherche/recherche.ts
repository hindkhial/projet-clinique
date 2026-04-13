import { Component, inject, resource, signal } from '@angular/core';
import { Patient } from '../../../shared/models/patient.model';
import { PatientService } from '../../../shared/services/patient.service';
import { firstValueFrom } from 'rxjs';
import { EditModal } from './edit-modal/edit-modal';
import { DeleteModal } from './delete-modal/delete-modal';
import { CreateModal } from './create-modal/create-modal';

@Component({
  selector: 'app-recherche',
  standalone: true,
  imports: [EditModal, DeleteModal, CreateModal],
  templateUrl: './recherche.html',
})
export class Recherche {
  private patientService = inject(PatientService);

  searchTerm = signal('');
  editingPatient = signal<Patient | null>(null);
  deletingPatient = signal<Patient | null>(null);
  creatingPatient = signal(false);
  successMessage = signal<string | null>(null);

  onCreated(message: string): void {
    this.successMessage.set(message);
    // reload
    this.patientsResource.reload();
    setTimeout(() => this.successMessage.set(null), 4000);
  }

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
