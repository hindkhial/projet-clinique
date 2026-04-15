import { Component, inject, input, output, signal, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { firstValueFrom } from 'rxjs';
import { RdvService } from '../../../../shared/services/rdv.service';
import { PatientService } from '../../../../shared/services/patient.service';
import { MedecinService } from '../../../../shared/services/medecin.service';
import { RdvResponse } from '../../../../shared/models/rdv.models';
import { Patient } from '../../../../shared/models/patient.model';
import { MedecinResponseDto } from '../../../../shared/models/medecin.models';
import { extractApiError } from '../../../../shared/utils/api-error.util';
import { PatientAutocomplete } from '../patient-autocomplete/patient-autocomplete';

@Component({
  selector: 'app-rdv-edit-modal',
  standalone: true,
  imports: [ReactiveFormsModule, PatientAutocomplete],
  templateUrl: './edit-modal.html',
})
export class RdvEditModal implements OnInit {
  private fb = inject(FormBuilder);
  private rdvService = inject(RdvService);
  private patientService = inject(PatientService);
  private medecinService = inject(MedecinService);

  rdv = input.required<RdvResponse>();
  saved = output<void>();
  cancelled = output<void>();

  step = signal<1 | 2 | 3>(1);

  patients = signal<Patient[]>([]);
  medecins = signal<MedecinResponseDto[]>([]);
  isLoadingOptions = signal(false);

  isLoadingDispos = signal(false);
  dispoError = signal<string | null>(null);
  disponibilites = signal<string[]>([]);
  selectedCreneau = signal<string | null>(null);

  isSubmitting = signal(false);
  submitError = signal<string | null>(null);

  step1Form = this.fb.group({
    idPatient: [null as number | null, [Validators.required]],
    idMedecin: [null as number | null, [Validators.required]],
    date:      ['', Validators.required],
  });

  ngOnInit(): void {
    const rdv = this.rdv();
    const date = rdv.dateRdv.substring(0, 10);

    this.step1Form.patchValue({
      idPatient: rdv.idPatient,
      idMedecin: rdv.idMedecin,
      date,
    });

    this.isLoadingOptions.set(true);
    Promise.all([
      firstValueFrom(this.patientService.getAll()),
      firstValueFrom(this.medecinService.getAll()),
    ]).then(([patients, medecins]) => {
      this.patients.set(patients);
      this.medecins.set(medecins);
      this.isLoadingOptions.set(false);
    }).catch(() => {
      this.isLoadingOptions.set(false);
    });
  }

  loadCreneaux(): void {
    if (this.step1Form.invalid) return;

    const { idMedecin, date } = this.step1Form.value;
    this.isLoadingDispos.set(true);
    this.dispoError.set(null);
    this.disponibilites.set([]);
    this.selectedCreneau.set(null);

    this.rdvService.getDisponibilites(idMedecin!, date!).subscribe({
      next: (slots) => {
        // réinjecter le créneau actuel s'il n'est pas dans la liste
        const current = this.rdv().dateRdv;
        const liste = !slots.includes(current)
          ? [current, ...slots].sort()
          : slots;

        if (liste.length === 0) {
          this.dispoError.set('Aucun créneau disponible pour ce médecin à cette date.');
        } else {
          this.disponibilites.set(liste);
          this.selectedCreneau.set(current);
          this.step.set(2);
        }
        this.isLoadingDispos.set(false);
      },
      error: (err) => {
        this.dispoError.set(extractApiError(err));
        this.isLoadingDispos.set(false);
      },
    });
  }

  getPatientLabel(id: number): string {
    const p = this.patients().find(p => p.id === id);
    return p ? `${p.nom} ${p.prenom} — ${p.email}` : `Patient #${id}`;
  }

  getMedecinLabel(id: number): string {
    const m = this.medecins().find(m => m.id === id);
    return m ? `Dr. ${m.nom} ${m.prenom}` : `Médecin #${id}`;
  }

  formatHeure(isoSlot: string): string {
    return new Date(isoSlot).toLocaleTimeString('fr-FR', {
      hour: '2-digit', minute: '2-digit'
    });
  }

  formatDateHeader(): string {
    const date = this.step1Form.value.date;
    if (!date) return '';
    return new Date(date).toLocaleDateString('fr-FR', {
      weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'
    });
  }

  formatDateRecap(): string {
    if (!this.selectedCreneau()) return '';
    return new Date(this.selectedCreneau()!).toLocaleDateString('fr-FR', {
      weekday: 'long', year: 'numeric', month: 'long',
      day: 'numeric', hour: '2-digit', minute: '2-digit'
    });
  }

  goBack(): void {
    this.step.set(1);
    this.selectedCreneau.set(null);
    this.submitError.set(null);
  }

  goToRecap(): void {
    if (!this.selectedCreneau()) return;
    this.step.set(3);
  }

  submit(): void {
    if (!this.selectedCreneau()) return;
    this.isSubmitting.set(true);
    this.submitError.set(null);

    const { idPatient, idMedecin } = this.step1Form.value;
    const dateRdv = this.selectedCreneau()!.replace('Z', '');

    this.rdvService.update(this.rdv().id, {
      idPatient: idPatient!,
      idMedecin: idMedecin!,
      dateRdv,
    }).subscribe({
      next: () => {
        this.saved.emit();
      },
      error: (err) => {
        this.submitError.set(extractApiError(err));
        this.isSubmitting.set(false);
      },
    });
  }
}
