package com.company.payments.model.request;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public record SearchPaymentRequest(
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startCreationDate,

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endCreationDate,

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startPaymentDate,

        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime endPaymentDate,

        String status,

        Integer paginate,

        Integer page
) {
}
