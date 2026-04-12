import { Component, input, output, inject, signal, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MedecinResponseDto, MedecinUpdateRequestAuth } from '../../../models/medecin.models';
import { MedecinService } from '../../../services/medecin.service';
import { extractApiError } from '../../../../shared/utils/api-error.util';
import { Specialite } from '../../../../shared/models/emuns';

@Component({
  selector: 'app-edit-modal',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './edit-modal.html',
})
export class EditModal implements OnInit {
  private fb = inject(FormBuilder);
  private medecinService = inject(MedecinService);

  medecin = input.required<MedecinResponseDto>();
  saved = output<void>();
  cancelled = output<void>();

  isSubmitting = signal(false);
  submitError = signal<string | null>(null);

  specialites: Specialite[] = ['GENERALISTE', 'RADIOLOGUE'];

  form = this.fb.group({
    nom:           ['', Validators.required],
    prenom:        ['', Validators.required],
    tel:           ['', [Validators.required, Validators.pattern(/^0[1-9]\d{8}$/)]],
    email:         ['', [Validators.required, Validators.email]],
    dateNaissance: ['', Validators.required],
    specialite:    ['' as Specialite, Validators.required],
    password:      [''],
  });

  ngOnInit(): void {
    const m = this.medecin();
    this.form.patchValue({
      nom:           m.nom,
      prenom:        m.prenom,
      tel:           m.tel,
      email:         m.email,
      dateNaissance: m.dateNaissance,
      specialite:    m.specialite,
    });
  }

  submit(): void {
    if (this.form.invalid) return;
    this.isSubmitting.set(true);
    this.submitError.set(null);

    const val = this.form.value;
    const payload: MedecinUpdateRequestAuth = {
      nom:           val.nom!,
      prenom:        val.prenom!,
      tel:           val.tel!,
      email:         val.email!,
      dateNaissance: val.dateNaissance!,
      specialite:    val.specialite! as Specialite,
      ...(val.password ? { password: val.password } : {}),
    };

    this.medecinService.update(this.medecin().id, payload).subscribe({
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
