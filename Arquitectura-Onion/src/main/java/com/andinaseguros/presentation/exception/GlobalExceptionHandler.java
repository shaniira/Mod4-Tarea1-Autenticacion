package com.andinaseguros.presentation.exception;

import com.andinaseguros.domain.exception.DomainException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.util.*;
import org.slf4j.*;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    public record ApiError(
            OffsetDateTime timestamp,
            int status,
            String codigo,
            String mensaje,
            String path,
            Map<String, String> campos) {}

    @ExceptionHandler(DomainException.class)
    ResponseEntity<ApiError> domain(DomainException exception, HttpServletRequest request) {
        HttpStatus status =
                switch (exception.getCodigo()) {
                    case "RECURSO_NO_ENCONTRADO" -> HttpStatus.NOT_FOUND;
                    case "CREDENCIALES_INVALIDAS" -> HttpStatus.UNAUTHORIZED;
                    default -> HttpStatus.UNPROCESSABLE_ENTITY;
                };

        return ResponseEntity.status(status)
                .body(
                        new ApiError(
                                OffsetDateTime.now(),
                                status.value(),
                                exception.getCodigo(),
                                exception.getMessage(),
                                request.getRequestURI(),
                                Map.of()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> validation(
            MethodArgumentNotValidException exception, HttpServletRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();

        exception
                .getBindingResult()
                .getFieldErrors()
                .forEach(
                        fieldError ->
                                fieldErrors.put(
                                        fieldError.getField(), fieldError.getDefaultMessage()));

        return ResponseEntity.badRequest()
                .body(
                        new ApiError(
                                OffsetDateTime.now(),
                                400,
                                "DATOS_INVALIDOS",
                                "La solicitud contiene datos inválidos",
                                request.getRequestURI(),
                                fieldErrors));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> illegal(
            IllegalArgumentException exception, HttpServletRequest request) {
        return ResponseEntity.badRequest()
                .body(
                        new ApiError(
                                OffsetDateTime.now(),
                                400,
                                "ARGUMENTO_INVALIDO",
                                exception.getMessage(),
                                request.getRequestURI(),
                                Map.of()));
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ApiError> generic(Exception exception, HttpServletRequest request) {
        log.error("Error no controlado en {}", request.getRequestURI(), exception);

        return ResponseEntity.status(500)
                .body(
                        new ApiError(
                                OffsetDateTime.now(),
                                500,
                                "ERROR_INTERNO",
                                "Ocurrió un error interno",
                                request.getRequestURI(),
                                Map.of()));
    }
}
