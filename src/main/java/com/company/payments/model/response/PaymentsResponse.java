package com.company.payments.model.response;

import com.company.payments.model.Payment;
import lombok.Builder;

import java.util.List;

@Builder
public record PaymentsResponse(
    List<Payment> data
) {

}
