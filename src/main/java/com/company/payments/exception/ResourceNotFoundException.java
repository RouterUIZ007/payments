package com.company.payments.exception;

public class ResourceNotFoundException extends RuntimeException {
    // Constructor con un mensaje de error
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Constructor con mensaje y causa
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}