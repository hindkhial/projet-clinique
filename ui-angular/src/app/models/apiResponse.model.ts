export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  messages?: string[];
  errors?: ErrorDetail[];
  timestamp: string; // LocalDateTime serializes as ISO 8601 string
}

export interface ErrorDetail {
  field: string | null;
  message: string;
}
