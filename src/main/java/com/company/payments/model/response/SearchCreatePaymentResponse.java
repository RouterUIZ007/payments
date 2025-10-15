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
public class SearchCreatePaymentResponse {
    private Long paymentId;
    private Double amount;
    private String reference;
    private String description;
    private LocalDateTime dueDate;
    private String status;
    private String callBackURL;
    private String callbackACKID;
    private String cancelDescription;
    private String authorizationNumber;
    private String paymentDate;
}
