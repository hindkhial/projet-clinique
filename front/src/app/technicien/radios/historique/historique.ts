import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { forkJoin } from 'rxjs';
import { RadioResponse } from '../../../shared/models/radio.models';
import { RadioService } from '../../../shared/services/radio.service';
import { PatientService } from '../../../shared/services/patient.service';
import { MedecinService } from '../../../shared/services/medecin.service';
import { Patient } from '../../../shared/models/patient.model';
import { MedecinResponseDto } from '../../../shared/models/medecin.models';
import { DeleteModal } from './delete-modal/delete-modal';
import { environment } from '../../../../environments/environment';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-historique',
  standalone: true,
  imports: [DeleteModal, DatePipe],
  templateUrl: './historique.html',
})
export class Historique implements OnInit {
  private radioService = inject(RadioService);
  private patientService = inject(PatientService);
  private medecinService = inject(MedecinService);

  searchTerm = signal('');
  deletingRadio = signal<RadioResponse | null>(null);
  successMessage = signal<string | null>(null);
  radios = signal<RadioResponse[]>([]);
  patients = signal<Patient[]>([]);
  medecins = signal<MedecinResponseDto[]>([]);
  isLoading = signal(false);
  loadError = signal(false);

  ngOnInit(): void {
    this.loadAll();
  }

  loadAll(): void {
    this.isLoading.set(true);
    this.loadError.set(false);

    forkJoin({
      radios:   this.radioService.getAll(),
      patients: this.patientService.getAll(),
      medecins: this.medecinService.getAll(),
    }).subscribe({
      next: ({ radios, patients, medecins }) => {
        this.radios.set(radios);
        this.patients.set(patients);
        this.medecins.set(medecins);
        this.isLoading.set(false);
      },
      error: () => {
        this.loadError.set(true);
        this.isLoading.set(false);
      },
    });
  }

  filteredRadios = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    if (!term) return this.radios();
    return this.radios().filter(r =>
      this.getPatientNom(r.patientId).toLowerCase().includes(term)
    );
  });

  getPatientNom(id: number): string {
    const p = this.patients().find(p => p.id === id);
    return p ? `${p.nom} ${p.prenom}` : `—`;
  }

  getRadiologueNom(id: number): string {
    const m = this.medecins().find(m => m.id === id);
    return m ? `Dr. ${m.nom} ${m.prenom}` : `—`;
  }

  onDeleted(): void {
    this.deletingRadio.set(null);
    this.loadAll();
    this.successMessage.set('Radio supprimée avec succès.');
    setTimeout(() => this.successMessage.set(null), 4000);
  }

  downloadRadio(id: number): void {
    this.radioService.download(id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `radio_${id}.jpg`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: () => console.error('Erreur téléchargement'),
    });
  }

  statusBadge(status: string): string {
    switch (status?.toUpperCase()) {
      case 'UPLOADED':   return 'bg-secondary';
      case 'PROCESSING': return 'bg-warning text-dark';
      case 'PREDICTED':  return 'bg-info text-dark';
      case 'VALIDATED':  return 'bg-success';
      default:           return 'bg-secondary';
    }
  }

  statusLabel(status: string): string {
    switch (status?.toUpperCase()) {
      case 'UPLOADED':   return 'Uploadé';
      case 'PROCESSING': return 'En traitement';
      case 'PREDICTED':  return 'Prédit (IA)';
      case 'VALIDATED':  return 'Validé';
      default:           return status ?? '—';
    }
  }
}
