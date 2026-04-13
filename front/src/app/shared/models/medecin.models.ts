import { Role, Specialite } from '../../shared/models/enums';

// ── READ (staff service) ────────────────────────────────────
export interface MedecinResponseDto {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  tel: string;
  dateNaissance: string; // yyyy-MM-dd
  role: Role;
  staffId: number;
  specialite: Specialite;
}

// ── WRITE (auth service) ────────────────────────────────────
export interface MedecinRegistrationRequest {
  nom: string;
  prenom: string;
  tel: string;
  email: string;
  dateNaissance: string;
  specialite: Specialite;
  password: string;
}

export interface MedecinUpdateRequestAuth {
  nom: string;
  prenom: string;
  tel: string;
  email: string;
  dateNaissance: string;
  specialite: Specialite;
  password?: string; // optionnel
}

export interface MedecinAccountResult {
  staff: MedecinResponseDto;
  keycloakUserId: string;
}
