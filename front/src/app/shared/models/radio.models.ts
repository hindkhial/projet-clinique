export interface RadioResponse {
  id: number;
  patientId: number;
  patientNom: string;
  patientPrenom: string;
  radiologueId: number;
  dateRadio: string;
  fichierRadioUrl: string | null;
  notes: string | null;
  createdAt: string;
  updatedAt: string;
  status: string;
  typeMaladie: string | null;
  confidence: number | null;
  riskLevel: string | null;
  heatmapUrl: string | null;
  notesRadiologue: string | null;
}

export interface CreateRadioRequest {
  patientId: number;
  radiologueId: number;
  dateRadio: string;
  notes?: string;
}
