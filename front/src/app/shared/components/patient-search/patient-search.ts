import {Component, inject, output, resource, signal} from '@angular/core';
import {PatientService} from '../../services/patient.service';
import {Patient} from '../../models/patient.model';
import {firstValueFrom} from 'rxjs';

@Component({
  selector: 'app-patient-search',
  imports: [],
  templateUrl: './patient-search.html',
  styleUrl: './patient-search.css',
})
export class PatientSearch {private patientService = inject(PatientService);

  // signal qui contient ce que l'utilisateur tape
  searchTerm = signal('');

  // event émis vers le composant parent quand un patient est cliqué
  patientSelected = output<Patient>();

  // resource() : se relance automatiquement quand searchTerm change
  patientsResource = resource<Patient[], string>({
    params: (): string => this.searchTerm(),
    loader: (loaderParams) => {
      const q = loaderParams.params;
      return q.length >= 2
        ? firstValueFrom(this.patientService.search(q))
        : Promise.resolve([]);
    }
  });

  select(patient: Patient): void {
    this.patientSelected.emit(patient);  // notifie le parent
    this.searchTerm.set('');             // vide la recherche
  }
}
