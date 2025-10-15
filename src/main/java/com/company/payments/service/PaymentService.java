package com.company.payments.service;

import com.company.payments.model.Payment;
import com.company.payments.model.request.PaymentRequest;
import com.company.payments.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;



    public LocalDateTime formatDateString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }

    public Payment savePayment(PaymentRequest request) {
        // fecha hoy
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);
        //generar referencia
        String randomAN = UUID.randomUUID().toString().replace("-", "").substring(0, 30);
        String reference = randomAN.toUpperCase();

        var payment = Payment.builder()
                // lo que viene en el request
                .externalId(request.externalId())
                .amount(request.amount())
                .description(request.description())
                .dueDate(formatDateString(request.dueDate()))
                .callbackURL(request.callbackURL())
                .creationDate(formatDateString(formattedDate))
                .reference(reference)
                .status("01")
                .message("Payment created successfully")
                .build();

        return paymentRepository.save(payment);
    }

}
