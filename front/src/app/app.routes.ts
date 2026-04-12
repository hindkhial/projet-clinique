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
      // {
      //   path: 'dashboard',
      //   loadComponent: () =>
      //     import('./receptionniste/dashboard/dashboard').then(m => m.Dashboard),
      // },
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
];
