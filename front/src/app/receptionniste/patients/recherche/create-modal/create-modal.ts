import { Component, inject, output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PatientService } from '../../../../shared/services/patient.service';
import { extractApiError } from '../../../../shared/utils/api-error.util';

@Component({
  selector: 'app-create-modal',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './create-modal.html',
})
export class CreateModal {
  private fb = inject(FormBuilder);
  private patientService = inject(PatientService);

  created = output<string>();
  cancelled = output<void>();

  isSubmitting = signal(false);
  submitError = signal<string | null>(null);

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

    const payload = { ...this.form.value as any, password: 'password' };

    this.patientService.create(payload).subscribe({
      next: (result) => {
        this.created.emit(`Patient ${result.patient.nom} ${result.patient.prenom} créé avec succès.`);
      },
      error: (err) => {
        this.submitError.set(extractApiError(err));
        this.isSubmitting.set(false);
      },
    });
  }
}
