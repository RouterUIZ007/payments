package com.company.payments.service;

import com.company.payments.model.Payment;
import com.company.payments.model.request.PaymentRequest;
import com.company.payments.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    public List<Payment> getPayments() {
        return paymentRepository.findAll();
    }

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
        //genera authorizationNumber
        Random random = new Random();
        int authorizationNumber = 100000 + random.nextInt(900000);

        var payment = Payment.builder()
                // lo que viene en el request
                .externalId(request.externalId())
                .amount(request.amount())
                .description(request.description())
                .dueDate(formatDateString(request.dueDate()))
                .callbackURL(request.callbackURL())
                .authorizationNumber(Integer.toString( authorizationNumber))
                .creationDate(formatDateString(formattedDate))
                .reference(reference)
                .reference(reference)
                .status("01")
                .message("Payment created successfully")
                .build();

        return paymentRepository.save(payment);
    }

    public Payment findByReferenceAndPaymentId(String reference, Long paymentId) {
        return paymentRepository
                .findByReferenceAndPaymentId(reference, paymentId)
                .orElse(null);
    }

    public Page<Payment> findByStatusAndDates(
            LocalDateTime startCreationDate,
            LocalDateTime endCreationDate,
            LocalDateTime startPaymentDate,
            LocalDateTime endPaymentDate,
            String status,
            Integer paginate,
            Integer page
    ) {

        Specification<Payment> spec = (root, query, cb) ->
                cb.equal(root.get("status"), status);

        if (startCreationDate != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("creationDate"), startCreationDate));
        }

        if (endCreationDate != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("creationDate"), endCreationDate));
        }

        if (startPaymentDate != null) {
            spec = spec.and((root, query, cb) ->
                    cb.greaterThanOrEqualTo(root.get("dueDate"), startPaymentDate));
        }
        if (endPaymentDate != null) {
            spec = spec.and((root, query, cb) ->
                    cb.lessThanOrEqualTo(root.get("dueDate"), endPaymentDate));
        }
        Pageable pageable = PageRequest.of(page, paginate);
        return paymentRepository.findAll(spec, pageable);
    }
}
