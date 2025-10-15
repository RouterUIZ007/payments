package com.company.payments.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreatePaymentResponse {

    private Long paymentId;
    private String reference;
    private Double amount;
    private String description;
    private LocalDateTime creationDate;
    private String status;
    private String message;

}
