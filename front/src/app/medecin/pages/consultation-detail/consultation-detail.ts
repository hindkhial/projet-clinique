import { Component, inject, signal, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { ConsultationService } from '../../../shared/services/consultation.service';
import { RapportService } from '../../../shared/services/rapport.service';
import { PatientService } from '../../../shared/services/patient.service';
import { ConsultationResponse } from '../../../shared/models/consultation.models';
import { RapportResponse } from '../../../shared/models/rapport.models';
import { Patient } from '../../../shared/models/patient.model';
import { extractApiError } from '../../../shared/utils/api-error.util';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-consultation-detail',
  standalone: true,
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './consultation-detail.html',
})
export class ConsultationDetail implements OnInit {
  private route               = inject(ActivatedRoute);
  private router              = inject(Router);
  private fb                  = inject(FormBuilder);
  private consultationService = inject(ConsultationService);
  private rapportService      = inject(RapportService);
  private patientService      = inject(PatientService);

  consultation = signal<ConsultationResponse | null>(null);
  patient      = signal<Patient | null>(null);
  rapport      = signal<RapportResponse | null>(null);
  isLoading    = signal(false);
  loadError    = signal(false);

  isSubmitting  = signal(false);
  submitError   = signal<string | null>(null);
  submitSuccess = signal(false);

  isDownloading = signal(false);

  form = this.fb.group({
    observation:  ['', Validators.required],
    traitement:   ['', Validators.required],
    typeMaladie:  [''],
  });

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) { this.router.navigate(['/medecin/dashboard']); return; }
    this.loadData(id);
  }

  loadData(id: number): void {
    this.isLoading.set(true);
    this.loadError.set(false);

    forkJoin({
      consultation: this.consultationService.getById(id),
      patients:     this.patientService.getAll(),
    }).subscribe({
      next: ({ consultation, patients }) => {
        this.consultation.set(consultation);
        const p = patients.find(p => p.id === consultation.idPatient) ?? null;
        this.patient.set(p);
        this.isLoading.set(false);

        // prefill form si déjà traitée
        if (consultation.observation) {
          this.form.patchValue({
            observation: consultation.observation,
            traitement:  consultation.traitement ?? '',
            typeMaladie: consultation.typeMaladie ?? '',
          });
        }

        // charger le rapport si existe
        this.loadRapport(id);
      },
      error: () => {
        this.loadError.set(true);
        this.isLoading.set(false);
      },
    });
  }

  private loadRapport(consultationId: number): void {
    // on cherche le rapport lié à cette consultation
    // le back n'a pas d'endpoint /rapport/consultation/{id}
    // on passe par /rapport/patient/{id} et on filtre
    const patientId = this.consultation()?.idPatient;
    if (!patientId) return;

    this.rapportService.getByPatient(patientId).subscribe({
      next: (rapports) => {
        const r = rapports.find(r => r.consultationId === consultationId) ?? null;
        this.rapport.set(r);
      },
      error: () => {},
    });
  }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }

    const id = this.consultation()?.id;
    if (!id) return;

    this.isSubmitting.set(true);
    this.submitError.set(null);

    const { observation, traitement, typeMaladie } = this.form.value;

    this.consultationService.update(id, {
      observation: observation!,
      traitement:  traitement!,
      typeMaladie: typeMaladie || undefined,
    }).subscribe({
      next: (updated) => {
        this.consultation.set(updated);
        this.isSubmitting.set(false);
        this.submitSuccess.set(true);
        setTimeout(() => this.submitSuccess.set(false), 3000);
        // recharger le rapport — il peut être généré maintenant
        this.loadRapport(id);
      },
      error: (err) => {
        this.submitError.set(extractApiError(err));
        this.isSubmitting.set(false);
      },
    });
  }

  downloadRapport(): void {
    const r = this.rapport();
    if (!r) return;

    this.isDownloading.set(true);

    this.rapportService.download(r.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a   = document.createElement('a');
        a.href     = url;
        a.download = `rapport_${r.id}.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
        this.isDownloading.set(false);
      },
      error: () => this.isDownloading.set(false),
    });
  }

  goBack(): void {
    this.router.navigate(['/medecin/dashboard']);
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
}
