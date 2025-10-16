package com.company.payments.exception;

import com.company.payments.model.response.NoCreatePaymentResponse;

public class UnprocessableEntityException extends RuntimeException {
    private final NoCreatePaymentResponse paymentResponse;

    public UnprocessableEntityException(NoCreatePaymentResponse paymentResponse) {
        super(paymentResponse.getMessage()); // Usamos el mensaje del objeto para la excepción
        this.paymentResponse = paymentResponse;
    }

    public NoCreatePaymentResponse getPaymentResponse() {
        return paymentResponse;
    }
}