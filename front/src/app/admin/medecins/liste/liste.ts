import { Component, inject, resource, signal, computed } from '@angular/core';
import { MedecinResponseDto } from '../../models/medecin.models';
import { MedecinService } from '../../services/medecin.service';
import { firstValueFrom } from 'rxjs';
import { EditModal } from './edit-modal/edit-modal';
import { DeleteModal } from './delete-modal/delete-modal';
import { CreateModal } from './create-modal/create-modal';

@Component({
  selector: 'app-medecins-liste',
  standalone: true,
  imports: [EditModal, DeleteModal, CreateModal],
  templateUrl: './liste.html',
})
export class Liste {
  private medecinService = inject(MedecinService);

  searchTerm = signal('');
  creatingMedecin = signal(false);
  editingMedecin = signal<MedecinResponseDto | null>(null);
  deletingMedecin = signal<MedecinResponseDto | null>(null);
  successMessage = signal<string | null>(null);

  onCreated(message: string): void {
    this.successMessage.set(message);
    // reload
    this.medecinsResource.reload();
    setTimeout(() => this.successMessage.set(null), 4000);
  }

  medecinsResource = resource<MedecinResponseDto[], void>({
    loader: () => firstValueFrom(this.medecinService.getAll()),
  });

  filteredMedecins = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    const all = this.medecinsResource.value() ?? [];
    if (term.length < 2) return all;
    return all.filter(m =>
      m.nom.toLowerCase().includes(term) ||
      m.prenom.toLowerCase().includes(term) ||
      m.email.toLowerCase().includes(term) ||
      m.specialite.toLowerCase().includes(term)
    );
  });
}
