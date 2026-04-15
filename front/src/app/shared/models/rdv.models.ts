export interface RdvCreateRequest {
  idPatient: number;
  idMedecin: number;
  dateRdv: string; // ISO format : yyyy-MM-ddTHH:mm:ss
}

export interface RdvUpdateRequest {
  idPatient: number;
  idMedecin: number;
  dateRdv: string;
}

export interface RdvResponse {
  id: number;
  idPatient: number;
  idMedecin: number;
  dateRdv: string;
}
