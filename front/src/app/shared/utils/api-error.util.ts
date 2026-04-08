import { ApiResponse } from '../models/api-response.model';

export function extractApiError(err: any): string {
  const apiError = err.error as ApiResponse<null>;
  if (apiError?.errors?.length) {
    return apiError.errors
      .map(e => e.field ? `${e.field} : ${e.message}` : e.message)
      .join(' | ');
  }
  return 'Erreur serveur, veuillez réessayer.';
}
