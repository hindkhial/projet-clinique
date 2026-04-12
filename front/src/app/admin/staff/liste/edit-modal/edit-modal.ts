import { Component, input, output, inject, signal, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { StaffResponseDto, StaffUpdateRequestAuth } from '../../../models/staff.models';
import { StaffService } from '../../../services/staff.service';
import { extractApiError } from '../../../../shared/utils/api-error.util';
import { Role } from '../../../../shared/models/emuns';

@Component({
  selector: 'app-edit-modal',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './edit-modal.html',
})
export class EditModal implements OnInit {
  private fb = inject(FormBuilder);
  private staffService = inject(StaffService);

  staff = input.required<StaffResponseDto>();
  saved = output<void>();
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
    password:      [''],
  });

  ngOnInit(): void {
    const s = this.staff();
    this.form.patchValue({
      nom:           s.nom,
      prenom:        s.prenom,
      tel:           s.tel,
      email:         s.email,
      dateNaissance: s.dateNaissance,
      role:          s.role,
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.isSubmitting.set(true);
    this.submitError.set(null);

    const val = this.form.value;
    const payload: StaffUpdateRequestAuth = {
      nom:           val.nom!,
      prenom:        val.prenom!,
      tel:           val.tel!,
      email:         val.email!,
      dateNaissance: val.dateNaissance!,
      role:          val.role! as Role,
      ...(val.password ? { password: val.password } : {}),
    };

    this.staffService.update(this.staff().id, payload).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.saved.emit();
      },
      error: (err) => {
        this.submitError.set(extractApiError(err));
        this.isSubmitting.set(false);
      },
    });
  }
}
