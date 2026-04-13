import { Component, inject, signal, computed, resource } from '@angular/core';
import { firstValueFrom } from 'rxjs';
import { RdvService } from '../../../shared/services/rdv.service';
import { PatientService } from '../../../shared/services/patient.service';
import { MedecinService } from '../../../shared/services/medecin.service';
import { RdvResponse } from '../../../shared/models/rdv.models';
import { RdvCreateModal } from './create-modal/create-modal';
import { RdvEditModal } from './edit-modal/edit-modal';
import { RdvDeleteModal } from './delete-modal/delete-modal';

@Component({
  selector: 'app-rdv-liste',
  standalone: true,
  imports: [RdvCreateModal, RdvEditModal, RdvDeleteModal],
  templateUrl: './liste.html',
})
export class RdvListe {
  private rdvService = inject(RdvService);
  private patientService = inject(PatientService);
  private medecinService = inject(MedecinService);

  searchTerm = signal('');
  editingRdv = signal<RdvResponse | null>(null);
  deletingRdv = signal<RdvResponse | null>(null);
  creatingRdv = signal(false);
  successMessage = signal<string | null>(null);

  patientNames = signal<Map<number, string>>(new Map());
  medecinNames = signal<Map<number, string>>(new Map());

  rdvResource = resource<RdvResponse[], unknown>({
    params: () => ({}),
    loader: async () => {
      const rdvs = await firstValueFrom(this.rdvService.getAll());
      await this.resolveNames(rdvs);
      return rdvs;
    }
  });

  private async resolveNames(rdvs: RdvResponse[]): Promise<void> {
    const patientIds = [...new Set(rdvs.map(r => r.idPatient))];
    const medecinIds = [...new Set(rdvs.map(r => r.idMedecin))];

    const pMap = new Map<number, string>();
    const mMap = new Map<number, string>();

    await Promise.allSettled(
      patientIds.map(id =>
        firstValueFrom(this.patientService.getById(id))
          .then(p => pMap.set(id, `${p.nom} ${p.prenom} — ${p.email}`))
          .catch(() => pMap.set(id, `Patient #${id}`))
      )
    );

    await Promise.allSettled(
      medecinIds.map(id =>
        firstValueFrom(this.medecinService.getById(id))
          .then(m => mMap.set(id, `Dr. ${m.nom} ${m.prenom}`))
          .catch(() => mMap.set(id, `Médecin #${id}`))
      )
    );

    this.patientNames.set(pMap);
    this.medecinNames.set(mMap);
  }

  // tri par date + filtre recherche
  filteredRdvs = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    const rdvs = [...(this.rdvResource.value() ?? [])].sort(
      (a, b) => new Date(a.dateRdv).getTime() - new Date(b.dateRdv).getTime()
    );

    if (!term) return rdvs;

    return rdvs.filter(rdv => {
      const patient = this.patientNames().get(rdv.idPatient)?.toLowerCase() ?? '';
      const medecin = this.medecinNames().get(rdv.idMedecin)?.toLowerCase() ?? '';
      const date = this.formatDate(rdv.dateRdv).toLowerCase();
      return (
        patient.includes(term) ||
        medecin.includes(term) ||
        date.includes(term) ||
        rdv.id.toString().includes(term)
      );
    });
  });

  isPast(dateRdv: string): boolean {
    return new Date(dateRdv) < new Date();
  }

  formatDate(dateRdv: string): string {
    const d = new Date(dateRdv);
    return d.toLocaleDateString('fr-FR', {
      weekday: 'short', year: 'numeric',
      month: 'short', day: 'numeric',
      hour: '2-digit', minute: '2-digit'
    });
  }

  getPatientName(id: number): string {
    return this.patientNames().get(id) ?? `Patient #${id}`;
  }

  getMedecinName(id: number): string {
    return this.medecinNames().get(id) ?? `Médecin #${id}`;
  }

  onCreated(message: string): void {
    this.creatingRdv.set(false);
    this.successMessage.set(message);
    this.rdvResource.reload();
    setTimeout(() => this.successMessage.set(null), 4000);
  }

  onSaved(): void {
    this.editingRdv.set(null);
    this.successMessage.set('Rendez-vous modifié avec succès.');
    this.rdvResource.reload();
    setTimeout(() => this.successMessage.set(null), 4000);
  }

  onDeleted(): void {
    this.deletingRdv.set(null);
    this.successMessage.set('Rendez-vous supprimé avec succès.');
    this.rdvResource.reload();
    setTimeout(() => this.successMessage.set(null), 4000);
  }
}
