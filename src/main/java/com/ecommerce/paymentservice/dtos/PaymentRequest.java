package com.ecommerce.paymentservice.dtos;

import lombok.Data;

@Data
public class PaymentRequest {
    private String orderId;
    private Double amount;
    private String currency;
    private String gateway;
}
