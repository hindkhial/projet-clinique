import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { PatientService } from '../patient-service';
import { PatientModel } from '../../models/patient.model';
import { HttpErrorResponse } from '@angular/common/http';

interface BackendError {
  field: string;
  message: string;
}

interface ErrorResponse {
  errors: BackendError[];
  success: boolean;
  timestamp: string;
}

@Component({
  selector: 'app-patient-create',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './patient-create.html',
  styleUrl: './patient-create.css',
})
export class PatientCreate {
  patientForm: FormGroup;
  isSubmitting = false;
  errorMessage = '';
  fieldErrors: Map<string, string> = new Map();

  constructor(
    private fb: FormBuilder,
    private patientService: PatientService,
    private router: Router
  ) {
    this.patientForm = this.fb.group({
      nom: ['', [Validators.required, Validators.minLength(2)]],
      prenom: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      tel: ['', [Validators.required, Validators.pattern(/^[0-9]{10}$/)]],
      dateNaissance: ['', Validators.required]
    });
  }

  onSubmit(): void {
    if (this.patientForm.invalid) {
      this.patientForm.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.errorMessage = '';
    this.fieldErrors.clear();

    const patient: PatientModel = this.patientForm.value;

    this.patientService.addPatient(patient).subscribe({
      next: () => {
        this.router.navigate(['/patients']);
      },
      error: (error: HttpErrorResponse) => {
        this.isSubmitting = false;
        this.handleError(error);
      }
    });
  }

  private handleError(error: HttpErrorResponse): void {
    if (error.status === 400 && error.error?.errors) {
      // Gestion des erreurs de validation du backend
      const errorResponse = error.error as ErrorResponse;

      errorResponse.errors.forEach((err: BackendError) => {
        this.fieldErrors.set(err.field, err.message);

        // Marquer le champ comme invalide
        const control = this.patientForm.get(err.field);
        if (control) {
          control.setErrors({ backend: err.message });
          control.markAsTouched();
        }
      });

      this.errorMessage = 'Veuillez corriger les erreurs dans le formulaire';
    } else if (error.status === 0) {
      this.errorMessage = 'Impossible de se connecter au serveur. Vérifiez votre connexion.';
    } else {
      this.errorMessage = `Erreur lors de l'ajout du patient (${error.status})`;
    }

    console.error('Erreur complète:', error);
  }

  onCancel(): void {
    this.router.navigate(['/patients']);
  }

  getErrorMessage(fieldName: string): string {
    const control = this.patientForm.get(fieldName);

    // Vérifier d'abord les erreurs du backend
    if (this.fieldErrors.has(fieldName)) {
      return this.fieldErrors.get(fieldName)!;
    }

    if (control?.hasError('backend')) {
      return control.getError('backend');
    }

    if (control?.hasError('required')) {
      return 'Ce champ est requis';
    }
    if (control?.hasError('minlength')) {
      return 'Minimum 2 caractères requis';
    }
    if (control?.hasError('email')) {
      return 'Email invalide';
    }
    if (control?.hasError('pattern')) {
      return 'Format de téléphone invalide (10 chiffres)';
    }

    return '';
  }

  isFieldInvalid(fieldName: string): boolean {
    const control = this.patientForm.get(fieldName);
    return !!(control?.invalid && (control?.dirty || control?.touched)) || this.fieldErrors.has(fieldName);
  }

  // Nettoyer les erreurs du backend quand l'utilisateur modifie le champ
  onFieldChange(fieldName: string): void {
    this.fieldErrors.delete(fieldName);
    const control = this.patientForm.get(fieldName);
    if (control?.hasError('backend')) {
      const errors = { ...control.errors };
      delete errors['backend'];
      control.setErrors(Object.keys(errors).length > 0 ? errors : null);
    }
  }
}
