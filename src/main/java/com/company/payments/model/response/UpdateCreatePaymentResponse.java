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
public class UpdateCreatePaymentResponse {
    private Long paymentId;
    private LocalDateTime creationDate;
    private String reference;
    private String status;
    private String message;
    private String cancelDescription;
    private LocalDateTime updatedAt;
}
