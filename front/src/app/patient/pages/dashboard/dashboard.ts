import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { PatientService } from '../../../shared/services/patient.service';
import { ConsultationService } from '../../../shared/services/consultation.service';
import { RdvService } from '../../../shared/services/rdv.service';
import { RapportService } from '../../../shared/services/rapport.service';
import { AuthService } from '../../../auth/auth.service';
import { Patient } from '../../../shared/models/patient.model';
import { ConsultationResponse } from '../../../shared/models/consultation.models';
import { RdvResponse } from '../../../shared/models/rdv.models';
import { RapportResponse } from '../../../shared/models/rapport.models';
import { ApiResponse } from '../../../shared/models/api-response.model';
import { environment } from '../../../../environments/environment';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './dashboard.html',
})
export class Dashboard implements OnInit {
  private patientService      = inject(PatientService);
  private consultationService = inject(ConsultationService);
  private rdvService          = inject(RdvService);
  private rapportService      = inject(RapportService);
  private authService         = inject(AuthService);
  private http                = inject(HttpClient);
  private router              = inject(Router);

  patient       = signal<Patient | null>(null);
  consultations = signal<ConsultationResponse[]>([]);
  rdvs          = signal<RdvResponse[]>([]);
  rapports      = signal<RapportResponse[]>([]);
  isLoading     = signal(false);
  loadError     = signal(false);
  activeTab     = signal<'consultations' | 'rdv' | 'rapports'>('consultations');
  isDownloading = signal<number | null>(null);

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    const id = this.authService.getInternalId();
    if (!id) return;

    this.isLoading.set(true);
    this.loadError.set(false);

    forkJoin({
      patient:       this.http.get<ApiResponse<Patient>>(`${environment.apiUrl}/patient/me`).pipe(),
      consultations: this.consultationService.getAll(),
      rdvs:          this.rdvService.getAll(),
      rapports:      this.rapportService.getByPatient(id),
    }).subscribe({
      next: ({ patient, consultations, rdvs, rapports }) => {
        this.patient.set((patient as any).data);
        this.consultations.set(consultations.filter(c => c.idPatient === id));
        this.rdvs.set((rdvs as any).filter((r: RdvResponse) => r.idPatient === id));
        this.rapports.set(rapports);
        this.isLoading.set(false);
      },
      error: () => {
        this.loadError.set(true);
        this.isLoading.set(false);
      },
    });
  }

  downloadRapport(rapport: RapportResponse): void {
    if (!rapport.pdfFileUrl) return;
    this.isDownloading.set(rapport.id);

    this.rapportService.download(rapport.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a   = document.createElement('a');
        a.href     = url;
        a.download = `rapport_${rapport.id}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
        this.isDownloading.set(null);
      },
      error: () => this.isDownloading.set(null),
    });
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
    return new Date(rdv.dateRdv) > new Date() ? 'bg-primary' : 'bg-secondary';
  }

  rdvStatusLabel(rdv: RdvResponse): string {
    return new Date(rdv.dateRdv) > new Date() ? 'À venir' : 'Passé';
  }
}
