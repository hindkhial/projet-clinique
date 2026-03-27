package com.ehei.auth.handlers;

import com.ehei.auth.DTOS.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler() {
        this.objectMapper = new ObjectMapper();
        // Support pour LocalDateTime
        this.objectMapper.registerModule(new JavaTimeModule());
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

    @ExceptionHandler(FeignException.BadRequest.class)
    public ResponseEntity<ApiResponse<Void>> handleFeignBadRequest(FeignException.BadRequest ex) {
        String errorBody = ex.contentUTF8();
        log.warn("Erreur 400 du service Staff: {}", errorBody);

        try {
            // Parser l'erreur JSON du service Staff
            ApiResponse<?> staffError = objectMapper.readValue(errorBody, ApiResponse.class);


            // Retourner l'erreur telle quelle
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body((ApiResponse<Void>) staffError);

        } catch (Exception parseEx) {


            // Retourner au moins le message brut
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Erreur du service Staff: " + errorBody));
        }
    }

    @ExceptionHandler(FeignException.NotFound.class)
    public ResponseEntity<ApiResponse<Void>> handleFeignNotFound(FeignException.NotFound ex) {
        log.warn("Erreur 404 du service Staff");
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Ressource non trouvée"));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponse<Void>> handleFeignException(FeignException ex) {
        log.error("Erreur Feign [{}]: {}", ex.status(), ex.getMessage());
        return ResponseEntity
                .status(ex.status())
                .body(ApiResponse.error("Erreur de communication avec le service Staff"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(RuntimeException ex) {
        log.error("Erreur runtime: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Erreur serveur: " + ex.getMessage()));
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