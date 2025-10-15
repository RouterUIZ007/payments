package com.company.payments.controller;

import com.company.payments.model.request.PaymentRequest;
import com.company.payments.model.response.ApiResponse;
import com.company.payments.model.response.CreatePaymentResponse;
import com.company.payments.model.response.NoCreatePaymentResponse;
import com.company.payments.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(
                            "400",
                            "Validation failed",
                            payment
                    )
            );
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


}
