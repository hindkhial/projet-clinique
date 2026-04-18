import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RadioService } from '../../shared/services/radio.service';
import { AuthService } from '../../auth/auth.service';
import { RadioResponse } from '../../shared/models/radio.models';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-radios-liste',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './radios-liste.html',
})
export class RadiosListe implements OnInit {
  private radioService = inject(RadioService);
  private authService  = inject(AuthService);
  private router       = inject(Router);

  radios    = signal<RadioResponse[]>([]);
  isLoading = signal(false);
  loadError = signal(false);
  searchTerm = signal('');

  ngOnInit(): void {
    this.loadPredicted();
  }

  loadPredicted(): void {
    const id = this.authService.getInternalId();
    if (!id) return;

    this.isLoading.set(true);
    this.loadError.set(false);

    this.radioService.getPredicted(id).subscribe({
      next: (radios) => {
        this.radios.set(radios);
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
      `${r.patientNom} ${r.patientPrenom}`.toLowerCase().includes(term)
    );
  });

  goToValidation(id: number): void {
    this.router.navigate(['/radiologue/radios', id]);
  }

  riskBadge(level: string | null): string {
    switch (level?.toUpperCase()) {
      case 'LOW':    return 'bg-success';
      case 'MEDIUM': return 'bg-warning text-dark';
      case 'HIGH':   return 'bg-danger';
      default:       return 'bg-secondary';
    }
  }

  riskLabel(level: string | null): string {
    switch (level?.toUpperCase()) {
      case 'LOW':    return 'Faible';
      case 'MEDIUM': return 'Moyen';
      case 'HIGH':   return 'Élevé';
      default:       return level ?? '—';
    }
  }
}
