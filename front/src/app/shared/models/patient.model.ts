export interface Patient {
  id: number;
  nom: string;
  prenom: string;
  email: string;
  tel: string;
  dateNaissance: string;
}

export interface PatientCreateRequest {
  nom: string;
  prenom: string;
  tel: string;
  email: string;
  dateNaissance: string;
  password: string;
}

export interface PatientUpdateRequest {
  nom: string;
  prenom: string;
  tel: string;
  email: string;
  dateNaissance: string;
  password?: string; // optionnel — null = pas de changement
}

export interface PatientAccountResult {
  patient: Patient;
  keycloakId: string;
  success: boolean;
}
