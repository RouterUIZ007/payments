package com.company.payments.exception;

import com.company.payments.model.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Manejo de excepciones de autenticación (401)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentialsException(BadCredentialsException ex) {
        ApiResponse<?> response = new ApiResponse<>(
                "401",
                "Unauthorized",
                ex.getMessage()
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // Manejo de excepciones de entidad no procesable (422)
    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<ApiResponse<?>> handleUnprocessableEntityException(UnprocessableEntityException ex) {
        ApiResponse<?> response = new ApiResponse<>(
                String.valueOf(HttpStatus.UNPROCESSABLE_ENTITY.value()),
                "Entity processing failed",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    // Manejo de excepciones de argumento ilegal (400)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                "Invalid request",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Manejo de excepciones de acceso denegado (403)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex) {
        ApiResponse<Object> response = new ApiResponse<>(
                String.valueOf(HttpStatus.FORBIDDEN.value()),
                "Forbidden",
                null
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    // Manejo de excepciones de recurso no encontrado (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ApiResponse<?> response = new ApiResponse<>(
                String.valueOf(HttpStatus.NOT_FOUND.value()),
                "Payment not found",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Manejo de excepciones falta de parametros (400)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        String message = "Ho hay datos con los parametros. ";
        if (!ex.getParameterName().isEmpty()){
            String missingParam = ex.getParameterName();
            message = "Se requiere el  parametro: " + missingParam;
        }
        ApiResponse<?> response = new ApiResponse<>(
                String.valueOf(HttpStatus.NOT_FOUND.value()), // 400
                "Invalid request",
                message
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    // Manejo de excepciones de conflicto (409)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<String>> handleConflictException(ConflictException ex) {
        ApiResponse<String> response = new ApiResponse<>(
                String.valueOf(HttpStatus.CONFLICT.value()),
                "Payment already processed ",
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }



}
