import { Component, input, output, inject, signal } from '@angular/core';
import { StaffResponseDto } from '../../../models/staff.models';
import { StaffService } from '../../../services/staff.service';
import { extractApiError } from '../../../../shared/utils/api-error.util';

@Component({
  selector: 'app-delete-modal',
  standalone: true,
  imports: [],
  templateUrl: './delete-modal.html',
})
export class DeleteModal {
  private staffService = inject(StaffService);

  staff = input.required<StaffResponseDto>();
  deleted = output<void>();
  cancelled = output<void>();

  isDeleting = signal(false);
  deleteError = signal<string | null>(null);

  confirm(): void {
    this.isDeleting.set(true);
    this.deleteError.set(null);

    this.staffService.delete(this.staff().id).subscribe({
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
