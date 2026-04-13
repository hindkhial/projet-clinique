import { Role } from './enums';

// ── READ (staff service) ────────────────────────────────────
export interface StaffResponseDto {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  tel: string;
  dateNaissance: string; // yyyy-MM-dd
  role: Role;
}

// ── WRITE (auth service) ────────────────────────────────────
export interface StaffRegistrationRequest {
  nom: string;
  prenom: string;
  tel: string;
  email: string;
  dateNaissance: string;
  role: Role;
  password: string;
}

export interface StaffUpdateRequestAuth {
  nom: string;
  prenom: string;
  tel: string;
  email: string;
  dateNaissance: string;
  role: Role;
  password?: string; // optionnel
}

export interface StaffAccountResult {
  staff: StaffResponseDto;
  keycloakUserId: string;
}
