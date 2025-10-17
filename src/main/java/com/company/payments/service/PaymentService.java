package com.company.payments.service;

import com.company.payments.exception.ConflictException;
import com.company.payments.exception.ResourceNotFoundException;
import com.company.payments.exception.UnprocessableEntityException;
import com.company.payments.model.Payment;
import com.company.payments.model.request.PaymentRequest;
import com.company.payments.model.response.*;
import com.company.payments.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public LocalDateTime formatDateString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.parse(date, formatter);
    }

    public List<Payment> findPayments() {
        return paymentRepository.findAll();
    }

    public ApiResponse<?> savePayment(PaymentRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errorsResult = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage).collect(Collectors.toList());
            String errors = String.join(", ", errorsResult);
            throw new UnprocessableEntityException(NoCreatePaymentResponse.builder()
                    .status("-1").message("Payment not created: " + errors).build()) {
            };
        }
        if (LocalDate.parse(request.dueDate(), formatter).isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha dueDate debe ser mayor a la fecha de hoy") {
            };
        }
        //generar referencia
        String reference = UUID.randomUUID().toString().replace("-", "").substring(0, 30).toUpperCase();

        var payment = Payment.builder()
                .externalId(request.externalId())
                .amount(request.amount())
                .description(request.description())
                .dueDate(formatDateString(request.dueDate()))
                .callbackURL(request.callbackURL())
                .authorizationNumber(Integer.toString(100000 + new Random().nextInt(900000)))
                .creationDate(formatDateString(LocalDateTime.now().format(formatter)))
                .reference(reference)
                .status("01")
                .message("Payment created successfully")
                .updatedAt(formatDateString(LocalDateTime.now().format(formatter)))
                .build();
        var paymentResponse = paymentRepository.save(payment);
        return new ApiResponse<>(
                "201", "Payment created successfully",
                CreatePaymentResponse.builder()
                        .paymentId(paymentResponse.getPaymentId())
                        .reference(paymentResponse.getReference())
                        .amount(paymentResponse.getAmount())
                        .description(paymentResponse.getDescription())
                        .creationDate(formatDateString(paymentResponse.getCreationDate().format(formatter)))
                        .status(paymentResponse.getStatus())
                        .message(paymentResponse.getMessage())
                        .build()
        );

    }

    public ApiResponse<?> findByReferenceAndPaymentId(String reference, Long paymentId) {
        var payment = paymentRepository
                .findByReferenceAndPaymentId(reference, paymentId)
                .orElse(null);
        if (payment == null) {
            throw new ResourceNotFoundException(
                    "El pago solicitado no existe o no se encontró con la referencia o ID dado.") {
            };
        }
        return new ApiResponse<>(
                "200", "Payment verified successfully",
                SearchCreatePaymentResponse.builder()
                        .paymentId(payment.getPaymentId())
                        .amount(payment.getAmount())
                        .reference(payment.getReference())
                        .description(payment.getDescription())
                        .dueDate(payment.getDueDate())
                        .status(payment.getStatus())
                        .callBackURL(payment.getCallbackURL())
                        .callbackACKID(payment.getCallbackACKID())
                        .cancelDescription(payment.getCancelDescription())
                        .authorizationNumber(payment.getAuthorizationNumber())
                        .paymentDate(payment.getPaymentDate())
                        .build()
        );
    }

    public ApiResponse<?> findByStatusAndDates(
            LocalDateTime startCreationDate, LocalDateTime endCreationDate,
            LocalDateTime startPaymentDate, LocalDateTime endPaymentDate,
            String status, Integer paginate, Integer page) throws MissingServletRequestParameterException {
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
        paginate = paginate == null ? 10 : paginate;
        page = page == null ? 0 : page;

        var response = paymentRepository.findAll(spec, PageRequest.of(page, paginate));

        if (response.getContent().isEmpty()) {
            throw new MissingServletRequestParameterException("", "") {
            };
        }

        return new ApiResponse<>(
                "200",
                "Payment retrieved successfully",
                new PageImpl<>(
                        response.getContent().stream()
                                .map(payment -> SearchCreatePaymentResponse.builder()
                                        .paymentId(payment.getPaymentId())
                                        .amount(payment.getAmount())
                                        .reference(payment.getReference())
                                        .description(payment.getDescription())
                                        .dueDate(payment.getDueDate())
                                        .status(payment.getStatus())
                                        .callBackURL(payment.getCallbackURL())
                                        .callbackACKID(payment.getCallbackACKID())
                                        .cancelDescription(payment.getCancelDescription())
                                        .authorizationNumber(payment.getAuthorizationNumber())
                                        .paymentDate(payment.getPaymentDate())
                                        .build())
                                .collect(Collectors.toList()),
                        response.getPageable(), response.getTotalElements())
        );
    }

    public Optional<Payment> findByReference(String reference) {
        return paymentRepository.findByReference(reference);
    }

    public ApiResponse<?> updatePayment(PaymentRequest request, BindingResult bindingResult) {

        Optional<Payment> paymentOptional = this.findByReference(request.reference());

        if (paymentOptional.isPresent()) {
            Payment res = paymentOptional.get();
            if (res.getStatus().equals("03")) {
                throw new ConflictException("El pago ya fue procesado y no puede modificarse") {
                };
            }
            res.setCancelDescription(request.updateDescription());
            res.setUpdatedAt(formatDateString(LocalDateTime.now().format(formatter)));
            res.setStatus(request.status());
            Payment response = paymentRepository.save(res);
            return new ApiResponse<>(
                    "202",
                    "Payment canceled successfully",
                    UpdateCreatePaymentResponse.builder()
                            .paymentId(response.getPaymentId())
                            .creationDate(formatDateString(response.getCreationDate().format(formatter)))
                            .reference(response.getReference())
                            .status(response.getStatus())
                            .message(response.getMessage())
                            .cancelDescription(response.getCancelDescription())
                            .updatedAt(response.getUpdatedAt())
                            .build()
            );
        }
        throw new ResourceNotFoundException("El pago solicitado no existe o no se encontró con la referencia o ID dado.") {};
    }
}
