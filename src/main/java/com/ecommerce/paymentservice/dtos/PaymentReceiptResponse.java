package com.ecommerce.paymentservice.dtos;

import com.ecommerce.paymentservice.model.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class PaymentReceiptResponse {
    private String receiptNumber;
    private String transactionId;
    private String orderId;
    private String gateway;
    private String gatewayPaymentId;
    private Long amountMinor;
    private String currency;
    private PaymentStatus status;
    private Instant paidAt;
}

