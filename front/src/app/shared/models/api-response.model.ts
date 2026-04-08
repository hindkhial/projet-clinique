export interface ErrorDetail {
  field: string | null;
  message: string;
}

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  messages?: string[];
  errors?: ErrorDetail[];
  timestamp: string;
}
