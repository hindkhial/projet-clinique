import { Component, inject, resource, signal, computed } from '@angular/core';
import { StaffResponseDto } from '../../models/staff.models';
import { StaffService } from '../../services/staff.service';
import { firstValueFrom } from 'rxjs';
import { EditModal } from './edit-modal/edit-modal';
import { DeleteModal } from './delete-modal/delete-modal';
import { CreateModal } from './create-modal/create-modal';

@Component({
  selector: 'app-staff-liste',
  standalone: true,
  imports: [EditModal, DeleteModal, CreateModal],
  templateUrl: './liste.html',
})
export class Liste {
  private staffService = inject(StaffService);

  searchTerm = signal('');
  creatingStaff = signal(false);
  editingStaff = signal<StaffResponseDto | null>(null);
  deletingStaff = signal<StaffResponseDto | null>(null);
  successMessage = signal<string | null>(null);

  onCreated(message: string): void {
    this.successMessage.set(message);
    // reload
    this.staffResource.reload();
    setTimeout(() => this.successMessage.set(null), 4000);
  }

  staffResource = resource<StaffResponseDto[], void>({
    loader: () => firstValueFrom(this.staffService.getAll()),
  });

  filteredStaff = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    const all = this.staffResource.value() ?? [];
    if (term.length < 2) return all;
    return all.filter(s =>
      s.nom.toLowerCase().includes(term) ||
      s.prenom.toLowerCase().includes(term) ||
      s.email.toLowerCase().includes(term)
    );
  });
}
