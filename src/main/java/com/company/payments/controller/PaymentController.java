package com.company.payments.controller;

import com.company.payments.exception.ConflictException;
import com.company.payments.exception.ResourceNotFoundException;
import com.company.payments.exception.UnprocessableEntityException;
import com.company.payments.model.Payment;
import com.company.payments.model.request.PaymentRequest;
import com.company.payments.model.response.*;
import com.company.payments.service.PaymentService;
import org.springframework.data.domain.Page;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;


    @PostMapping("/payment")
    public ResponseEntity<?> createPayment(
            @Valid
            @RequestBody
            PaymentRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {

            List<String> errorsResult = bindingResult.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.toList());
            String errors = String.join(", ", errorsResult);

            NoCreatePaymentResponse payment = NoCreatePaymentResponse.builder()
                    .status("-1")
                    .message("Payment not created: " + errors)
                    .build();
            throw new UnprocessableEntityException(payment) {
            };
        }

        var payment = paymentService.savePayment(request);


        CreatePaymentResponse paymentResponse = CreatePaymentResponse.builder()
                .paymentId(payment.getPaymentId())
                .reference(payment.getReference())
                .amount(payment.getAmount())
                .description(payment.getDescription())
                .creationDate(payment.getCreationDate())
                .status(payment.getStatus())
                .message(payment.getMessage())
                .build();


        ApiResponse<CreatePaymentResponse> response = new ApiResponse<>(
                "201",
                "Payment created successfully",
                paymentResponse
        );

        URI location = URI.create("/payment");
        return ResponseEntity.created(location).body(response);

    }

    @GetMapping("/payment/{reference}/{paymentId}")
    public ResponseEntity<ApiResponse<?>> getPaymentByTypeAndId(
            @PathVariable String reference,
            @PathVariable Long paymentId
    ) {
        Payment payment = paymentService.findByReferenceAndPaymentId(
                reference.trim(), paymentId
        );
        if (payment == null) {
            throw new ResourceNotFoundException(
                    "El pago solicitado no existe o no " +
                            "se encontró con la referencia o ID dado.") {
            };
        }

        SearchCreatePaymentResponse paymentResponse = SearchCreatePaymentResponse.builder()
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
                .build();

        ApiResponse<SearchCreatePaymentResponse> response = new ApiResponse<>(
                "200",
                "Payment verified successfully",
                paymentResponse
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payments/search")
    public ResponseEntity<?> search(
            @RequestParam("startCreationDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime startCreationDate,
            @RequestParam("endCreationDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime endCreationDate,

            @RequestParam("startPaymentDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime startPaymentDate,
            @RequestParam("endPaymentDate")
            @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
            LocalDateTime endPaymentDate,

            @RequestParam("status") String status,
            @RequestParam(value = "paginate", defaultValue = "10") Integer paginate,
            @RequestParam(value = "page", defaultValue = "0") Integer page
    ) throws Exception {

        Page<Payment> resultados = paymentService.findByStatusAndDates(
                startCreationDate, endCreationDate,
                startPaymentDate, endPaymentDate,
                status, paginate, page
        );

        if (resultados.getContent().isEmpty()) {
            throw new MissingServletRequestParameterException("", "") {
            };
        }

        List<SearchCreatePaymentResponse> paymentResponsesList = resultados.getContent().stream()
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
                .collect(Collectors.toList());

        ApiResponse<Page<SearchCreatePaymentResponse>> response = new ApiResponse<>(
                "200",
                "Payment retrieved successfully",
                new PageImpl<>(paymentResponsesList, resultados.getPageable(), resultados.getTotalElements())
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/payment/cancel")
    public ResponseEntity<?> cancelPayment(
            @Valid
            @RequestBody
            PaymentRequest request,
            BindingResult bindingResult
    ) {

        Payment payment = paymentService.updatePayment(request);

        if (payment == null) {
            throw new ConflictException("El pago ya fue procesado y no puede modificarse") {
            };
        }

        ApiResponse<Payment> response = new ApiResponse<>(
                "202",
                "Payment canceled successfully",
                payment);

        URI location = URI.create("/payment/cancel/" + payment.getPaymentId());
        return ResponseEntity.accepted().location(location).body(response);
    }
}
