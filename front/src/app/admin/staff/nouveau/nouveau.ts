import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { StaffService } from '../../services/staff.service';
import { StaffAccountResult } from '../../models/staff.models';
import { extractApiError } from '../../../shared/utils/api-error.util';
import { Role } from '../../../shared/models/emuns';

@Component({
  selector: 'app-nouveau',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './nouveau.html',
})
export class Nouveau {
  private fb = inject(FormBuilder);
  private staffService = inject(StaffService);

  isSubmitting = signal(false);
  submitError = signal<string | null>(null);
  createdResult = signal<StaffAccountResult | null>(null);

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
    this.createdResult.set(null);

    this.staffService.create(this.form.value as any).subscribe({
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
