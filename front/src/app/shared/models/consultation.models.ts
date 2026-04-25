export type ConsultationType = 'NORMAL' | 'RADIO' | 'ANALYSES';

export interface ConsultationResponse {
  id: number;
  idPatient: number;
  idMedecin: number;
  idRdv: number | null;
  type: ConsultationType;
  typeMaladie: string | null;
  confidence: number | null;
  riskLevel: string | null;
  observation: string | null;
  traitement: string | null;
  dateConsultation: string;
}

export interface ConsultationCreateRequest {
  idPatient: number;
  idMedecin: number;
  idRdv?: number;
}

export interface ConsultationUpdateRequest {
  observation: string;
  traitement: string;
  typeMaladie?: string;
}
