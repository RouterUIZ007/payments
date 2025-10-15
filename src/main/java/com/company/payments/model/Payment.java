package com.company.payments.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    @Column(nullable = false)
    private Double amount;

    @Column(unique = false)
    private String description;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dueDate;

    private String callbackURL;
    private String callbackACKID;
    private String externalId;
    private String reference;
    private LocalDateTime creationDate;
    private String status;
    private String message;
    private String cancelDescription;
    private String paymentDate;
    private String authorizationNumber;
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;
}
