import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { ConsultationService } from '../../../shared/services/consultation.service';
import { RdvService } from '../../../shared/services/rdv.service';
import { PatientService } from '../../../shared/services/patient.service';
import { AuthService } from '../../../auth/auth.service';
import { ConsultationResponse } from '../../../shared/models/consultation.models';
import { RdvResponse } from '../../../shared/models/rdv.models';
import { Patient } from '../../../shared/models/patient.model';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  private consultationService = inject(ConsultationService);
  private rdvService          = inject(RdvService);
  private patientService      = inject(PatientService);
  private authService         = inject(AuthService);
  private router              = inject(Router);

  consultations = signal<ConsultationResponse[]>([]);
  rdvs          = signal<RdvResponse[]>([]);
  patients      = signal<Patient[]>([]);
  isLoading     = signal(false);
  loadError     = signal(false);
  searchTerm    = signal('');
  activeTab     = signal<'consultations' | 'rdv'>('consultations');

  consultationsTraitees = computed(() => this.consultations().filter(c => c.observation).length);
  consultationsEnAttente = computed(() => this.consultations().filter(c => !c.observation).length);

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    const id = this.authService.getInternalId();
    if (!id) return;

    this.isLoading.set(true);
    this.loadError.set(false);

    forkJoin({
      consultations: this.consultationService.getByMedecin(id),
      rdvs:          this.rdvService.getAll(),
      patients:      this.patientService.getAll(),
    }).subscribe({
      next: ({ consultations, rdvs, patients }) => {
        this.consultations.set(consultations);
        this.rdvs.set(rdvs.filter(r => r.idMedecin === id));
        this.patients.set(patients);
        this.isLoading.set(false);
      },
      error: () => {
        this.loadError.set(true);
        this.isLoading.set(false);
      },
    });
  }

  filteredConsultations = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    if (!term) return this.consultations();
    return this.consultations().filter(c =>
      this.getPatientNom(c.idPatient).toLowerCase().includes(term)
    );
  });

  filteredRdvs = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    if (!term) return this.rdvs();
    return this.rdvs().filter(r =>
      this.getPatientNom(r.idPatient).toLowerCase().includes(term)
    );
  });

  getPatientNom(id: number): string {
    const p = this.patients().find(p => p.id === id);
    return p ? `${p.nom} ${p.prenom}` : '—';
  }

  goToConsultation(id: number): void {
    this.router.navigate(['/medecin/consultations', id]);
  }

  typeBadge(type: string): string {
    switch (type?.toUpperCase()) {
      case 'NORMAL':   return 'bg-secondary';
      case 'RADIO':    return 'bg-info text-dark';
      case 'ANALYSES': return 'bg-warning text-dark';
      default:         return 'bg-secondary';
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

  rdvStatusBadge(rdv: RdvResponse): string {
    const date = new Date(rdv.dateRdv);
    const now  = new Date();
    return date > now ? 'bg-primary' : 'bg-secondary';
  }

  rdvStatusLabel(rdv: RdvResponse): string {
    const date = new Date(rdv.dateRdv);
    const now  = new Date();
    return date > now ? 'À venir' : 'Passé';
  }
}
