import { Component, inject, resource, signal, computed } from '@angular/core';
import { StaffResponseDto } from '../../models/staff.models';
import { StaffService } from '../../services/staff.service';
import { firstValueFrom } from 'rxjs';
import { EditModal } from './edit-modal/edit-modal';
import { DeleteModal } from './delete-modal/delete-modal';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-staff-liste',
  standalone: true,
  imports: [RouterLink, EditModal, DeleteModal],
  templateUrl: './liste.html',
})
export class Liste {
  private staffService = inject(StaffService);

  searchTerm = signal('');
  editingStaff = signal<StaffResponseDto | null>(null);
  deletingStaff = signal<StaffResponseDto | null>(null);

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
