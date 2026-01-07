import { Routes } from '@angular/router';
import {PatientsPage} from './patients/patients-page/patients-page';
import {PatientCreate} from './patients/patient-create/patient-create';
import {PatientUpdate} from './patients/patient-update/patient-update';

export const routes: Routes = [
  { path: 'patients', component: PatientsPage },
  { path: 'patients/create', component: PatientCreate },
  { path: 'patients/edit/:id', component: PatientUpdate }
];
