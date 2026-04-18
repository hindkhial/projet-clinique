import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RadioService } from '../../shared/services/radio.service';
import { RadioResponse, RadioValidationRequest } from '../../shared/models/radio.models';
import { extractApiError } from '../../shared/utils/api-error.util';
import { DatePipe } from '@angular/common';
import { forkJoin } from 'rxjs';
import { PatientService } from '../../shared/services/patient.service';
import { Patient } from '../../shared/models/patient.model';


@Component({
  selector: 'app-radio-validation',
  standalone: true,
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './radio-validation.html',
})
export class RadioValidation implements OnInit {
  private route        = inject(ActivatedRoute);
  private router       = inject(Router);
  private fb           = inject(FormBuilder);
  private radioService = inject(RadioService);
  private patientService = inject(PatientService);

  radio       = signal<RadioResponse | null>(null);
  isLoading   = signal(false);
  loadError   = signal(false);
  isSubmitting = signal(false);
  submitError  = signal<string | null>(null);
  submitSuccess = signal(false);
  patient = signal<Patient | null>(null);
  isModifie = signal(false);

  // blobs images
  radioImageUrl   = signal<string | null>(null);
  heatmapUrl      = signal<string | null>(null);
  imagesLoading   = signal(false);

  form = this.fb.group({
    decision:    ['CONFIRME', Validators.required],
    typeMaladie: [''],
    confidence:  [null as number | null],
    riskLevel:   [''],
    commentaire: [''],
  });


  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) { this.router.navigate(['/radiologue/radios']); return; }
    this.loadRadio(id);
  }

  loadRadio(id: number): void {
    this.isLoading.set(true);
    this.loadError.set(false);

    forkJoin({
      radio:   this.radioService.getById(id),
      patients: this.patientService.getAll(),
    }).subscribe({
      next: ({ radio, patients }) => {
        this.radio.set(radio);
        const p = patients.find(p => p.id === radio.patientId) ?? null;
        this.patient.set(p);
        this.isLoading.set(false);
        this.prefillForm(radio);
        this.loadImages(id);
      },
      error: () => {
        this.loadError.set(true);
        this.isLoading.set(false);
      },
    });
  }

  private prefillForm(radio: RadioResponse): void {
    this.form.patchValue({
      typeMaladie: radio.typeMaladie ?? '',
      confidence:  radio.confidence,
      riskLevel:   radio.riskLevel ?? '',
      commentaire: radio.notesRadiologue ?? '',
    });
  }

  private loadImages(id: number): void {
    this.imagesLoading.set(true);

    this.radioService.download(id).subscribe({
      next: (blob) => {
        this.radioImageUrl.set(URL.createObjectURL(blob));
        this.loadHeatmap(id);
      },
      error: () => this.imagesLoading.set(false),
    });
  }

  private loadHeatmap(id: number): void {
    this.radioService.downloadHeatmap(id).subscribe({
      next: (blob) => {
        this.heatmapUrl.set(URL.createObjectURL(blob));
        this.imagesLoading.set(false);
      },
      error: () => this.imagesLoading.set(false),
    });
  }

  setDecision(decision: 'CONFIRME' | 'MODIFIE'): void {
    this.form.patchValue({ decision });
    this.isModifie.set(decision === 'MODIFIE');

    if (decision === 'MODIFIE') {
      this.form.get('typeMaladie')?.setValidators(Validators.required);
      this.form.get('riskLevel')?.setValidators(Validators.required);
    } else {
      this.form.get('typeMaladie')?.clearValidators();
      this.form.get('riskLevel')?.clearValidators();
    }
    this.form.get('typeMaladie')?.updateValueAndValidity();
    this.form.get('riskLevel')?.updateValueAndValidity();
  }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }

    const id = this.radio()?.id;
    if (!id) return;

    this.isSubmitting.set(true);
    this.submitError.set(null);

    const { decision, typeMaladie, confidence, riskLevel, commentaire } = this.form.value;

    const req: RadioValidationRequest = {
      decision: decision as 'CONFIRME' | 'MODIFIE',
      ...(decision === 'MODIFIE' && {
        typeMaladie: typeMaladie ?? undefined,
        confidence:  confidence ?? undefined,
        riskLevel:   riskLevel ?? undefined,
        commentaire: commentaire ?? undefined,
      }),
      ...(decision === 'CONFIRME' && commentaire && { commentaire }),
    };

    this.radioService.validate(id, req).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.submitSuccess.set(true);
        setTimeout(() => this.router.navigate(['/radiologue/radios']), 2000);
      },
      error: (err) => {
        this.submitError.set(extractApiError(err));
        this.isSubmitting.set(false);
      },
    });
  }

  goBack(): void {
    this.router.navigate(['/radiologue/radios']);
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
