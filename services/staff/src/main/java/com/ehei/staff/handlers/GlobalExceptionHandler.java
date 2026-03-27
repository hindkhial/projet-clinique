package com.ehei.staff.handlers;

import com.ehei.staff.DTOS.ApiResponse;
import com.ehei.staff.exceptions.EmailAlreadyExistException;
import com.ehei.staff.exceptions.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleEntityNotFoundException(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        List<ApiResponse.ErrorDetail> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.add(new ApiResponse.ErrorDetail(fieldName, errorMessage));
        });
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errors));
    }

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailAlreadyExistsException(
            EmailAlreadyExistException ex) {
        List<ApiResponse.ErrorDetail> errors = new ArrayList<>();
        errors.add(new ApiResponse.ErrorDetail("email", ex.getMessage()));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errors));
    }

    // Add this handler for resource not found (swagger resources, favicon, etc.)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            NoResourceFoundException ex,
            HttpServletRequest request) throws NoResourceFoundException {

        String path = request.getRequestURI();

        // Don't intercept swagger/api-docs/favicon requests
        if (path.contains("/swagger-ui") ||
                path.contains("/v3/api-docs") ||
                path.contains("/favicon.ico")) {
            throw ex; // Let Spring handle it
        }

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Resource not found"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        String path = request.getRequestURI();

        // Don't intercept swagger/api-docs requests
        if (path.contains("/swagger-ui") || path.contains("/v3/api-docs")) {
            throw new RuntimeException(ex); // Re-throw to let Spring/Springdoc handle it
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur : " + ex.getMessage()));
    }
}