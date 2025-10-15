package com.company.payments.model.request;

import jakarta.validation.Valid;

import java.util.List;

public record PaymentsRequest(
        @Valid
        List<PaymentRequest> data
) {
}
