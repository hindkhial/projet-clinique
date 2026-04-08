import { Component, input, output, inject, signal } from '@angular/core';
import { Patient } from '../../../../shared/models/patient.model';
import { PatientService } from '../../../../shared/services/patient.service';
import { extractApiError } from '../../../../shared/utils/api-error.util';

@Component({
  selector: 'app-delete-modal',
  imports: [],
  templateUrl: './delete-modal.html',
  standalone: true
})
export class DeleteModal {
  private patientService = inject(PatientService);

  patient = input.required<Patient>();
  deleted = output<void>();
  cancelled = output<void>();

  isDeleting = signal(false);
  deleteError = signal<string | null>(null);

  confirm(): void {
    this.isDeleting.set(true);
    this.deleteError.set(null);

    this.patientService.delete(this.patient().id).subscribe({
      next: () => {
        this.isDeleting.set(false);
        this.deleted.emit();
      },
      error: (err) => {
        this.deleteError.set(extractApiError(err));
        this.isDeleting.set(false);
      },
    });
  }
}
