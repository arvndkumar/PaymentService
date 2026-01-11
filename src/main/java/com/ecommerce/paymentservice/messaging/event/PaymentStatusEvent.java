package com.ecommerce.paymentservice.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class PaymentStatusEvent {
    private String orderId;
    private String transactionId;
    private String gatewayPaymentId;
    private String status;
    private String gateway;
    private Long amountMinor;
    private String currency;
    private Instant eventAt;
}
