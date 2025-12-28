package com.ehei.staff.DTOS;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private List<String> messages;
    private List<ErrorDetail> errors;
    private LocalDateTime timestamp;

    // Success response with data
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Success response with data and message
    public static <T> ApiResponse<T> success(T data, String message) {
        List<String> messages = new ArrayList<>();
        messages.add(message);
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .messages(messages)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Success response with only message
    public static <T> ApiResponse<T> success(String message) {
        List<String> messages = new ArrayList<>();
        messages.add(message);
        return ApiResponse.<T>builder()
                .success(true)
                .messages(messages)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Error response with single error
    public static <T> ApiResponse<T> error(String errorMessage) {
        List<ErrorDetail> errors = new ArrayList<>();
        errors.add(new ErrorDetail(null, errorMessage));
        return ApiResponse.<T>builder()
                .success(false)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Error response with multiple errors
    public static <T> ApiResponse<T> error(List<ErrorDetail> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    // Error response with field-specific error
    public static <T> ApiResponse<T> error(String field, String message) {
        List<ErrorDetail> errors = new ArrayList<>();
        errors.add(new ErrorDetail(field, message));
        return ApiResponse.<T>builder()
                .success(false)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetail {
        private String field;
        private String message;
    }
}