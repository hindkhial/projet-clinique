import { Component, input, output, inject, signal } from '@angular/core';
import { RadioResponse } from '../../../../shared/models/radio.models';
import { RadioService } from '../../../../shared/services/radio.service';
import { extractApiError } from '../../../../shared/utils/api-error.util';
import {CommonModule } from "@angular/common";

@Component({
  selector: 'app-delete-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './delete-modal.html',
})
export class DeleteModal {
  private radioService = inject(RadioService);

  radio = input.required<RadioResponse>();
  deleted = output<void>();
  cancelled = output<void>();

  isDeleting = signal(false);
  deleteError = signal<string | null>(null);

  confirm(): void {
    this.isDeleting.set(true);
    this.deleteError.set(null);

    this.radioService.delete(this.radio().id).subscribe({
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
