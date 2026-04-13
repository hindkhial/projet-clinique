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
];
