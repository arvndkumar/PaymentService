package com.ecommerce.paymentservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(
        name = "payment_transactions",
        indexes = {
                @Index(name = "idx_payment_order_id", columnList = "orderId", unique = true),
                @Index(name = "idx_payment_gateway_payment_id", columnList = "gatewayPaymentId")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, length = 64)
    private String orderId;

    @Column(nullable = false, length = 16)
    private String gateway; // STRIPE / RAZORPAY / etc

    /**
     * Stripe: PaymentIntent id
     * Razorpay: Order id
     */
    @Column(length = 128)
    private String gatewayPaymentId;

    @Column(nullable = false)
    private Long amountMinor; // cents/paise

    @Column(nullable = false, length = 8)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private PaymentStatus status;

    @Column(length = 64)
    private String receiptNumber;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        if (id == null) id = UUID.randomUUID().toString();
        if (createdAt == null) createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
