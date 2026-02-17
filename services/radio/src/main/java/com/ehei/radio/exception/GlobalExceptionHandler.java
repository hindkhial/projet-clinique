package com.ehei.radio.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RadioNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRadioNotFound(RadioNotFoundException ex) {
        log.error("Erreur: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                404,
                "Not Found",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }


    @ExceptionHandler(InvalidRadioException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRadio(InvalidRadioException ex) {
        log.error("Erreur: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                400,
                "Bad Request",
                ex.getMessage()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllOtherErrors(
            Exception ex,
            HttpServletRequest request
    ) {
        String path = request.getRequestURI();

        // Don't intercept swagger/api-docs requests
        if (path.contains("/swagger-ui") || path.contains("/v3/api-docs")) {
            throw new RuntimeException(ex);
        }

        log.error("Erreur inattendue: {}", ex.getMessage());

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                500,
                "Internal Server Error",
                "Une erreur s'est produite"
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}