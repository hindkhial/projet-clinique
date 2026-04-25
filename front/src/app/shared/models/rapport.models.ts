export interface RapportResponse {
  id: number;
  patientId: number;
  patientNom: string;
  patientPrenom: string;
  patientEmail: string;
  medecinId: number;
  medecinNom: string;
  medecinPrenom: string;
  medecinSpecialite: string;
  typeRapport: string;
  dateRapport: string;
  typeMaladie: string | null;
  observations: string | null;
  traitement: string | null;
  diagnostic: string | null;
  consultationId: number | null;
  pdfFileUrl: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface CreateRapportRequest {
  patientId: number;
  medecinId: number;
  typeRapport: string;
  dateRapport: string;
  observations?: string;
  traitement?: string;
  diagnostic?: string;
  consultationId?: number;
}
