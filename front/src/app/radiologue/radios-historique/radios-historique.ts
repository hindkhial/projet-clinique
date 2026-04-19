import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { RadioService } from '../../shared/services/radio.service';
import { PatientService } from '../../shared/services/patient.service';
import { AuthService } from '../../auth/auth.service';
import { RadioResponse } from '../../shared/models/radio.models';
import { Patient } from '../../shared/models/patient.model';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-radios-historique',
  standalone: true,
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './radios-historique.html',
})
export class RadiosHistorique implements OnInit {
  private radioService  = inject(RadioService);
  private patientService = inject(PatientService);
  private authService   = inject(AuthService);
  private router        = inject(Router);
  private fb            = inject(FormBuilder);

  radios    = signal<RadioResponse[]>([]);
  patients  = signal<Patient[]>([]);
  isLoading = signal(false);
  loadError = signal(false);

  filters = this.fb.group({
    search:    [''],
    statut:    [''],
    riskLevel: [''],
    dateFrom:  [''],
    dateTo:    [''],
  });

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    const id = this.authService.getInternalId();
    if (!id) return;

    this.isLoading.set(true);
    this.loadError.set(false);

    forkJoin({
      radios:   this.radioService.getByRadiologue(id),
      patients: this.patientService.getAll(),
    }).subscribe({
      next: ({ radios, patients }) => {
        this.radios.set(radios);
        this.patients.set(patients);
        this.isLoading.set(false);
      },
      error: () => {
        this.loadError.set(true);
        this.isLoading.set(false);
      },
    });
  }

  filteredRadios = computed(() => {
    const { search, statut, riskLevel, dateFrom, dateTo } = this.filters.value;
    const term = search?.toLowerCase().trim() ?? '';

    return this.radios().filter(r => {
      // filtre nom patient
      if (term) {
        const nom = this.getPatientNom(r.patientId).toLowerCase();
        if (!nom.includes(term)) return false;
      }

      // filtre statut
      if (statut && r.status?.toUpperCase() !== statut) return false;

      // filtre risque
      if (riskLevel && r.riskLevel?.toUpperCase() !== riskLevel) return false;

      // filtre date from
      if (dateFrom) {
        const from = new Date(dateFrom);
        const date = new Date(r.dateRadio);
        if (date < from) return false;
      }

      // filtre date to
      if (dateTo) {
        const to = new Date(dateTo);
        to.setHours(23, 59, 59);
        const date = new Date(r.dateRadio);
        if (date > to) return false;
      }

      return true;
    });
  });

  getPatientNom(id: number): string {
    const p = this.patients().find(p => p.id === id);
    return p ? `${p.nom} ${p.prenom}` : '—';
  }

  goToValidation(id: number): void {
    this.router.navigate(['/radiologue/radios', id]);
  }

  resetFilters(): void {
    this.filters.reset();
  }

  hasActiveFilters = computed(() => {
    const { search, statut, riskLevel, dateFrom, dateTo } = this.filters.value;
    return !!(search || statut || riskLevel || dateFrom || dateTo);
  });

  statusBadge(status: string): string {
    switch (status?.toUpperCase()) {
      case 'UPLOADED':   return 'bg-secondary';
      case 'PROCESSING': return 'bg-warning text-dark';
      case 'PREDICTED':  return 'bg-info text-dark';
      case 'VALIDATED':  return 'bg-success';
      default:           return 'bg-secondary';
    }
  }

  statusLabel(status: string): string {
    switch (status?.toUpperCase()) {
      case 'UPLOADED':   return 'Uploadé';
      case 'PROCESSING': return 'En traitement';
      case 'PREDICTED':  return 'Prédit (IA)';
      case 'VALIDATED':  return 'Validé';
      default:           return status ?? '—';
    }
  }

  riskBadge(level: string | null): string {
    switch (level?.toUpperCase()) {
      case 'LOW':    return 'bg-success';
      case 'MEDIUM': return 'bg-warning text-dark';
      case 'HIGH':   return 'bg-danger';
      default:       return 'bg-secondary';
    }
  }

  riskLabel(level: string | null): string {
    switch (level?.toUpperCase()) {
      case 'LOW':    return 'Faible';
      case 'MEDIUM': return 'Moyen';
      case 'HIGH':   return 'Élevé';
      default:       return level ?? '—';
    }
  }
}
