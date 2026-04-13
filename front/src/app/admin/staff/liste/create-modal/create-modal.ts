import { Component, inject, output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { StaffService } from '../../../services/staff.service';
import { extractApiError } from '../../../../shared/utils/api-error.util';
import { Role } from '../../../../shared/models/enums';

@Component({
  selector: 'app-create-modal',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './create-modal.html',
})
export class CreateModal {
  private fb = inject(FormBuilder);
  private staffService = inject(StaffService);

  created = output<string>();
  cancelled = output<void>();

  isSubmitting = signal(false);
  submitError = signal<string | null>(null);

  roles: Role[] = ['MEDECIN', 'RECEPTIONNISTE', 'TECHNICIEN'];

  form = this.fb.group({
    nom:           ['', Validators.required],
    prenom:        ['', Validators.required],
    tel:           ['', [Validators.required, Validators.pattern(/^0[1-9]\d{8}$/)]],
    email:         ['', [Validators.required, Validators.email]],
    dateNaissance: ['', Validators.required],
    role:          ['' as Role, Validators.required],
    password:      ['', [Validators.required, Validators.minLength(8)]],
  });

  submit(): void {
    if (this.form.invalid) return;
    this.isSubmitting.set(true);
    this.submitError.set(null);

    this.staffService.create(this.form.value as any).subscribe({
      next: (result) => {
        this.created.emit(`Membre ${result.staff.nom} ${result.staff.prenom} créé avec succès.`);
      },
      error: (err) => {
        this.submitError.set(extractApiError(err));
        this.isSubmitting.set(false);
      },
    });
  }
}
