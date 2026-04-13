import { Component, input, output, inject, signal } from '@angular/core';
import { RdvResponse } from '../../../../shared/models/rdv.models';
import { RdvService } from '../../../../shared/services/rdv.service';
import { extractApiError } from '../../../../shared/utils/api-error.util';

@Component({
  selector: 'app-rdv-delete-modal',
  standalone: true,
  imports: [],
  templateUrl: './delete-modal.html',
})
export class RdvDeleteModal {
  private rdvService = inject(RdvService);

  rdv = input.required<RdvResponse>();
  patientName = input.required<string>();
  medecinName = input.required<string>();
  deleted = output<void>();
  cancelled = output<void>();

  isDeleting = signal(false);
  deleteError = signal<string | null>(null);

  formatDate(dateRdv: string): string {
    const d = new Date(dateRdv);
    return d.toLocaleDateString('fr-FR', {
      weekday: 'long', year: 'numeric',
      month: 'long', day: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }

  confirm(): void {
    this.isDeleting.set(true);
    this.deleteError.set(null);

    this.rdvService.delete(this.rdv().id).subscribe({
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
