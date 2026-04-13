import { Component, inject, output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MedecinService } from '../../../services/medecin.service';
import { extractApiError } from '../../../../shared/utils/api-error.util';
import { Specialite } from '../../../../shared/models/enums';

@Component({
  selector: 'app-create-modal',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './create-modal.html',
})
export class CreateModal {
  private fb = inject(FormBuilder);
  private medecinService = inject(MedecinService);

  created = output<string>();
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
    password:      ['', [Validators.required, Validators.minLength(8)]],
  });

  submit(): void {
    if (this.form.invalid) return;
    this.isSubmitting.set(true);
    this.submitError.set(null);

    this.medecinService.create(this.form.value as any).subscribe({
      next: (result) => {
        this.isSubmitting.set(false); // ← ajouter cette ligne
        this.created.emit(`Médecin ${result.staff.nom} ${result.staff.prenom} créé avec succès.`);
      },
      error: (err) => {
        this.submitError.set(extractApiError(err));
        this.isSubmitting.set(false);
      },
    });
  }
}
