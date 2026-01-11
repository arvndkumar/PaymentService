package com.ecommerce.paymentservice.services.gateway;

public interface PaymentGatewaySelector {

    PaymentGateway getPaymentGateway(String gatewayName);
}
