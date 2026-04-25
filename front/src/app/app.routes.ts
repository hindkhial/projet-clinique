import { Routes } from '@angular/router';
import { Login } from './auth/login/login/login';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: Login },

  {
    path: 'receptionniste',
    loadComponent: () =>
      import('./receptionniste/layout/receptionniste-layout/receptionniste-layout').then(m => m.ReceptionnisteLayout),
    children: [
      { path: '', redirectTo: 'patients/recherche', pathMatch: 'full' },
      {
        path: 'patients/recherche',
        loadComponent: () =>
          import('./receptionniste/patients/recherche/recherche').then(m => m.Recherche),
      },
      {
        path: 'rdv',
        loadComponent: () =>
          import('./receptionniste/rdv/liste/liste').then(m => m.RdvListe),
      },
    ],
  },

  {
    path: 'admin',
    loadComponent: () =>
      import('./admin/layout/admin-layout/admin-layout').then(m => m.AdminLayout),
    children: [
      { path: '', redirectTo: 'staff', pathMatch: 'full' },
      {
        path: 'staff',
        loadComponent: () =>
          import('./admin/staff/liste/liste').then(m => m.Liste),
      },
      {
        path: 'medecins',
        loadComponent: () =>
          import('./admin/medecins/liste/liste').then(m => m.Liste),
      },
    ],
  },

  {
    path: 'technicien',
    loadComponent: () =>
      import('./technicien/layout/technicien-layout/technicien-layout').then(m => m.TechnicienLayout),
    children: [
      { path: '', redirectTo: 'radios/upload', pathMatch: 'full' },
      {
        path: 'radios/upload',
        loadComponent: () =>
          import('./technicien/radios/upload/upload').then(m => m.Upload),
      },
      {
        path: 'radios/historique',
        loadComponent: () =>
          import('./technicien/radios/historique/historique').then(m => m.Historique),
      },
    ],
  },

  {
    path: 'radiologue',
    loadComponent: () =>
      import('./radiologue/layout/radiologue-layout/radiologue-layout').then(m => m.RadiologueLayout),
    children: [
      { path: '', redirectTo: 'radios', pathMatch: 'full' },
      {
        path: 'radios',
        loadComponent: () =>
          import('./radiologue/radio-liste/radios-liste').then(m => m.RadiosListe),
      },
      {
        path: 'radios/historique',
        loadComponent: () =>
          import('./radiologue/radios-historique/radios-historique').then(m => m.RadiosHistorique),
      },
      {
        path: 'radios/:id',
        loadComponent: () =>
          import('./radiologue/radio-validation/radio-validation').then(m => m.RadioValidation),
      },
    ],
  },

  {
    path: 'medecin',
    loadComponent: () =>
      import('./medecin/layout/medecin-layout/medecin-layout').then(m => m.MedecinLayout),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./medecin/pages/dashboard/dashboard').then(m => m.Dashboard),
      },
      {
        path: 'consultations/:id',
        loadComponent: () =>
          import('./medecin/pages/consultation-detail/consultation-detail').then(m => m.ConsultationDetail),
      },
    ],
  },

  {
    path: 'patient',
    loadComponent: () =>
      import('./patient/layout/patient-layout/patient-layout').then(m => m.PatientLayout),
    children: [
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./patient/pages/dashboard/dashboard').then(m => m.Dashboard),
      },
    ],
  },
];
