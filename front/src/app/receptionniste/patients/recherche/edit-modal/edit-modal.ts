import { Component, input, output, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Patient, PatientUpdateRequest } from '../../../../shared/models/patient.model';
import { PatientService } from '../../../../shared/services/patient.service';
import { extractApiError } from '../../../../shared/utils/api-error.util';

@Component({
  selector: 'app-edit-modal',
  imports: [ReactiveFormsModule],
  templateUrl: './edit-modal.html',
  standalone: true
})
export class EditModal {
  private fb = inject(FormBuilder);
  private patientService = inject(PatientService);

  patient = input.required<Patient>();
  saved = output<void>();
  cancelled = output<void>();

  isUpdating = signal(false);
  updateError = signal<string | null>(null);

  form = this.fb.group({
    nom:           ['', Validators.required],
    prenom:        ['', Validators.required],
    tel:           ['', [Validators.required, Validators.pattern(/^0[1-9]\d{8}$/)]],
    email:         ['', [Validators.required, Validators.email]],
    dateNaissance: ['', Validators.required],
  });

  ngOnInit(): void {
    this.form.patchValue(this.patient());
  }

  submit(): void {
    if (this.form.invalid) return;
    this.isUpdating.set(true);
    this.updateError.set(null);

    this.patientService.update(this.patient().id, this.form.value as PatientUpdateRequest).subscribe({
      next: () => {
        this.isUpdating.set(false);
        this.saved.emit();
      },
      error: (err) => {
        this.updateError.set(extractApiError(err));
        this.isUpdating.set(false);
      },
    });
  }
}
