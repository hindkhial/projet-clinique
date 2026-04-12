import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PatientService } from '../../../shared/services/patient.service';
import { PatientAccountResult } from '../../../shared/models/patient.model';
import { extractApiError } from '../../../shared/utils/api-error.util';

@Component({
  selector: 'app-nouveau',
  imports: [ReactiveFormsModule],
  templateUrl: './nouveau.html',
  standalone: true
})
export class Nouveau {
  private fb = inject(FormBuilder);
  private patientService = inject(PatientService);

  isSubmitting = signal(false);
  submitError = signal<string | null>(null);
  createdResult = signal<PatientAccountResult | null>(null);

  form = this.fb.group({
    nom:           ['', Validators.required],
    prenom:        ['', Validators.required],
    tel:           ['', [Validators.required, Validators.pattern(/^0[1-9]\d{8}$/)]],
    email:         ['', [Validators.required, Validators.email]],
    dateNaissance: ['', Validators.required],
  });

  submit(): void {
    if (this.form.invalid) return;
    this.isSubmitting.set(true);
    this.submitError.set(null);
    this.createdResult.set(null);

    const payload = {
      ...this.form.value as any,
      password: 'password',
    };

    this.patientService.create(payload).subscribe({
      next: (result) => {
        this.createdResult.set(result);
        this.isSubmitting.set(false);
        this.form.reset();
      },
      error: (err) => {
        this.submitError.set(extractApiError(err));
        this.isSubmitting.set(false);
      },
    });
  }
}
