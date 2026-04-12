import { Component, input, output, inject, signal } from '@angular/core';
import { MedecinResponseDto } from '../../../models/medecin.models';
import { MedecinService } from '../../../services/medecin.service';
import { extractApiError } from '../../../../shared/utils/api-error.util';

@Component({
  selector: 'app-delete-modal',
  standalone: true,
  imports: [],
  templateUrl: './delete-modal.html',
})
export class DeleteModal {
  private medecinService = inject(MedecinService);

  medecin = input.required<MedecinResponseDto>();
  deleted = output<void>();
  cancelled = output<void>();

  isDeleting = signal(false);
  deleteError = signal<string | null>(null);

  confirm(): void {
    this.isDeleting.set(true);
    this.deleteError.set(null);

    this.medecinService.delete(this.medecin().id).subscribe({
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
