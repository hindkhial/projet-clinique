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
        path: 'patients/nouveau',
        loadComponent: () =>
          import('./receptionniste/patients/nouveau/nouveau').then(m => m.Nouveau),
      },
      {
        path: 'patients/recherche',
        loadComponent: () =>
          import('./receptionniste/patients/recherche/recherche').then(m => m.Recherche),
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
        path: 'staff/nouveau',
        loadComponent: () =>
          import('./admin/staff/nouveau/nouveau').then(m => m.Nouveau),
      },
      {
        path: 'medecins',
        loadComponent: () =>
          import('./admin/medecins/liste/liste').then(m => m.Liste),
      },
      {
        path: 'medecins/nouveau',
        loadComponent: () =>
          import('./admin/medecins/nouveau/nouveau').then(m => m.Nouveau),
      },
    ],
  },
];
