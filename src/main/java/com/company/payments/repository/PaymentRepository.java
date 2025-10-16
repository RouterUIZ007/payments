package com.company.payments.repository;

import com.company.payments.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long>, JpaSpecificationExecutor<Payment> {

    Optional<Payment> findByReferenceAndPaymentId(String reference, Long paymentId);

    Optional<Payment> findByReference(String reference);


}
