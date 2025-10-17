package com.company.payments.controller;

import com.company.payments.model.request.PaymentRequest;
import com.company.payments.model.request.SearchPaymentRequest;
import com.company.payments.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

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
