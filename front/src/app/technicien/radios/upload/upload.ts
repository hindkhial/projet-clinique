import { Component, inject, signal } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { RadioService } from '../../../shared/services/radio.service';
import { PatientService } from '../../../shared/services/patient.service';
import { MedecinService } from '../../../shared/services/medecin.service';
import { Patient } from '../../../shared/models/patient.model';
import { MedecinResponseDto } from '../../../shared/models/medecin.models';
import { extractApiError } from '../../../shared/utils/api-error.util';
import { debounceTime, distinctUntilChanged, switchMap, of } from 'rxjs';
import { DatePipe } from "@angular/common";

@Component({
  selector: 'app-upload',
  standalone: true,
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './upload.html',
})
export class Upload {
  private fb = inject(FormBuilder);
  private radioService = inject(RadioService);
  private patientService = inject(PatientService);
  private medecinService = inject(MedecinService);

  // ── Étapes ──────────────────────────────────────────────
  currentStep = signal<1 | 2 | 3>(1);

  // ── Étape 1 : Patient autocomplete ──────────────────────
  patientSearch = new FormControl('');
  patientResults = signal<Patient[]>([]);
  selectedPatient = signal<Patient | null>(null);
  showPatientDropdown = signal(false);

  // ── Étape 1 : Radiologues ────────────────────────────────
  radiologues = signal<MedecinResponseDto[]>([]);
  radiologuesLoading = signal(false);

  // ── Formulaire étape 1 ───────────────────────────────────
  step1Form = this.fb.group({
    radiologueId: ['', Validators.required],
    dateRadio:    ['', Validators.required],
    notes:        [''],
  });

  // ── Étape 2 : Fichier ────────────────────────────────────
  selectedFile = signal<File | null>(null);
  fileError = signal<string | null>(null);
  isDragging = signal(false);

  // ── Soumission ───────────────────────────────────────────
  isSubmitting = signal(false);
  submitError = signal<string | null>(null);
  successMessage = signal<string | null>(null);

  ngOnInit(): void {
    this.loadRadiologues();

    this.patientSearch.valueChanges.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(q => {
        if (!q || q.length < 2) { this.patientResults.set([]); return of([]); }
        return this.patientService.search(q);
      })
    ).subscribe(results => {
      this.patientResults.set(results);
      this.showPatientDropdown.set(results.length > 0);
    });
  }

  loadRadiologues(): void {
    this.radiologuesLoading.set(true);
    this.medecinService.getAll().subscribe({
      next: (medecins) => {
        this.radiologues.set(medecins.filter(m => m.specialite === 'RADIOLOGUE'));
        this.radiologuesLoading.set(false);
      },
      error: () => this.radiologuesLoading.set(false),
    });
  }

  selectPatient(patient: Patient): void {
    this.selectedPatient.set(patient);
    this.patientSearch.setValue(`${patient.nom} ${patient.prenom}`, { emitEvent: false });
    this.showPatientDropdown.set(false);
    this.patientResults.set([]);
  }

  clearPatient(): void {
    this.selectedPatient.set(null);
    this.patientSearch.setValue('');
  }

  // ── Étape 2 : Gestion fichier ────────────────────────────
  onFileChange(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files?.length) this.setFile(input.files[0]);
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();
    this.isDragging.set(true);
  }

  onDragLeave(): void {
    this.isDragging.set(false);
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDragging.set(false);
    const file = event.dataTransfer?.files[0];
    if (file) this.setFile(file);
  }

  private setFile(file: File): void {
    this.fileError.set(null);
    if (!file.type.startsWith('image/')) {
      this.fileError.set('Le fichier doit être une image (JPG, PNG, etc.).');
      return;
    }
    this.selectedFile.set(file);
  }

  removeFile(): void {
    this.selectedFile.set(null);
  }

  // ── Navigation étapes ────────────────────────────────────
  goToStep2(): void {
    if (!this.selectedPatient()) return;
    if (this.step1Form.invalid) { this.step1Form.markAllAsTouched(); return; }
    this.currentStep.set(2);
  }

  goToStep3(): void {
    if (!this.selectedFile()) { this.fileError.set('Veuillez sélectionner une image.'); return; }
    this.currentStep.set(3);
  }

  goBack(): void {
    this.currentStep.update(s => (s > 1 ? (s - 1) as 1 | 2 | 3 : s));
  }

  // ── Récap helpers ────────────────────────────────────────
  get selectedRadiologue(): MedecinResponseDto | undefined {
    const id = this.step1Form.get('radiologueId')?.value;
    return this.radiologues().find(r => r.id == (id as any));
  }

  // ── Soumission finale ────────────────────────────────────
  submit(): void {
    this.isSubmitting.set(true);
    this.submitError.set(null);

    const { radiologueId, dateRadio, notes } = this.step1Form.value;

    const radioData = {
      patientId:    this.selectedPatient()!.id,
      radiologueId: Number(radiologueId),
      dateRadio:    dateRadio,
      notes:        notes || undefined,
    };

    const formData = new FormData();
    formData.append('radioData', JSON.stringify(radioData));
    formData.append('file', this.selectedFile()!);

    this.radioService.upload(formData).subscribe({
      next: () => {
        this.successMessage.set('Radio soumise avec succès. L\'analyse IA est en cours.');
        this.resetForm();
      },
      error: (err) => {
        this.submitError.set(extractApiError(err));
        this.isSubmitting.set(false);
      },
    });
  }

  private resetForm(): void {
    this.isSubmitting.set(false);
    this.currentStep.set(1);
    this.step1Form.reset();
    this.selectedPatient.set(null);
    this.patientSearch.setValue('');
    this.selectedFile.set(null);
    setTimeout(() => this.successMessage.set(null), 5000);
  }
}
