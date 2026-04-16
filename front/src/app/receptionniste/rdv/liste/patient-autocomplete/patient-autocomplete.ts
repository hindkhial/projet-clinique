import { Component, input, output, signal, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Patient } from '../../../../shared/models/patient.model';

@Component({
  selector: 'app-patient-autocomplete',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './patient-autocomplete.html',
})
export class PatientAutocomplete {
  patients = input.required<Patient[]>();
  selectedId = input<number | null | undefined>(null);
  invalid = input<boolean | undefined>(false);

  selectedIdChange = output<number | null>();

  searchTerm = signal('');
  isOpen = signal(false);

  filteredPatients = computed(() => {
    const term = this.searchTerm().toLowerCase().trim();
    if (!term) return this.patients();
    return this.patients().filter(p =>
      p.nom.toLowerCase().includes(term) ||
      p.prenom.toLowerCase().includes(term) ||
      p.email.toLowerCase().includes(term)
    );
  });

  selectedLabel = computed(() => {
    const id = this.selectedId();
    if (!id) return '';
    const p = this.patients().find(p => p.id === id);
    return p ? `${p.nom} ${p.prenom} — ${p.email}` : '';
  });

  onInputClick(): void {
    if (this.selectedId()) {
      this.searchTerm.set('');
      this.selectedIdChange.emit(null);
    }
    this.isOpen.set(true);
  }

  onInput(value: string): void {
    this.searchTerm.set(value);
    this.isOpen.set(true);
    // si l'utilisateur efface tout, reset la sélection
    if (!value) this.selectedIdChange.emit(null);
  }

  select(patient: Patient): void {
    this.selectedIdChange.emit(patient.id);
    this.searchTerm.set(`${patient.nom} ${patient.prenom} — ${patient.email}`);
    this.isOpen.set(false);
  }

  onBlur(): void {
    // délai pour laisser le click sur l'option se déclencher
    setTimeout(() => {
      this.isOpen.set(false);
      // si rien de sélectionné, vider l'input
      if (!this.selectedId()) this.searchTerm.set('');
    }, 200);
  }
}
