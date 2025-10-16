package com.company.payments.model.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public record PaymentRequest(

        @NotNull(message = "amount no debe ser nulo")
        @DecimalMin(value = "0.01", message = "Debe ser una cantidad mayor a 0")
        Double amount,

        @NotBlank(message = "description no debe estar vacío")
        String description,

        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}", message = "Formato de fecha inválido. El formato esperado es yyyy-MM-dd HH:mm:ss")
        @NotBlank(message = "dueDate no debe estar vacío")
        String dueDate,

        @NotBlank(message = "callbackURL no debe estar vacío")
        String callbackURL,

        @NotBlank(message = "externalId no debe estar vacío")
        String externalId,

        String creationDate,
        String reference,
        String status,
        String message,
        String cancelDescription,
        String callbackACKID,
        String paymentDate,
        String authorizationNumber,
        String updateDescription,
        String updatedAt
) {
}
