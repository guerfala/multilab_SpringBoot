package com.example.multilab.Exception;

import com.example.multilab.DTO.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

/**
 * ═══════════════════════════════════════════════════════════
 * GlobalExceptionHandler — @ControllerAdvice
 *
 * Intercepte toutes les exceptions et renvoie des réponses
 * JSON structurées au lieu de stack traces 500
 *
 * Gère :
 * - ResourceNotFoundException → 404
 * - MethodArgumentNotValidException → 400 (validation)
 * - DateTimeParseException → 400 (format date)
 * - IllegalArgumentException → 400
 * - Exception générique → 500
 * ═══════════════════════════════════════════════════════════
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // ─── 404 : Ressource non trouvée ───
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        ApiError error = new ApiError(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // ─── 400 : Validation @Valid ───
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {

        String messages = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
            .collect(Collectors.joining(", "));

        ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Validation Error",
            messages,
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // ─── 400 : Format de date invalide ───
    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<ApiError> handleDateParse(
            DateTimeParseException ex, HttpServletRequest request) {

        ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            "Format de date invalide. Utilisez le format YYYY-MM-DD",
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // ─── 400 : Argument illégal ───
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex, HttpServletRequest request) {

        ApiError error = new ApiError(
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            request.getRequestURI()
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // ─── 500 : Erreur générique (fallback) ───
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex, HttpServletRequest request) {

        ApiError error = new ApiError(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "Une erreur interne est survenue. Veuillez réessayer.",
            request.getRequestURI()
        );

        // Logger l'erreur côté serveur
        ex.printStackTrace();

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
