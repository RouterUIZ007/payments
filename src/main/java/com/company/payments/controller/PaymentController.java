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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @PostMapping("/payment")
    public ResponseEntity<?> createPayment(@Valid @RequestBody PaymentRequest request, BindingResult bindingResult) {
        var payment = paymentService.savePayment(request, bindingResult);
        return ResponseEntity.created(URI.create("/payment")).body(payment);
    }

    @GetMapping("/payment/{reference}/{paymentId}")
    public ResponseEntity<?> getPaymentByTypeAndId(@PathVariable String reference, @PathVariable Long paymentId) {
        var response = paymentService.findByReferenceAndPaymentId(reference.trim(), paymentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/payments/search")
    public ResponseEntity<?> searchPayment(SearchPaymentRequest request) throws Exception {
        var response = paymentService.findByStatusAndDates(
                request.startCreationDate(), request.endCreationDate(),
                request.startPaymentDate(), request.endPaymentDate(),
                request.status(), request.paginate(), request.page()
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/payment/cancel")
    public ResponseEntity<?> cancelPayment(@Valid @RequestBody PaymentRequest request, BindingResult bindingResult) {
        var response = paymentService.updatePayment(request, bindingResult);
        return ResponseEntity.ok(response);
    }
}
