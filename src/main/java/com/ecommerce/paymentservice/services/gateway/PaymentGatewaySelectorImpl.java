package com.ecommerce.paymentservice.services.gateway;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class PaymentGatewaySelectorImpl implements PaymentGatewaySelector {

    private final Map<String, PaymentGateway> gateways;

    public PaymentGatewaySelectorImpl(
            StripeGateway stripe,
            RazorpayGateway razorpay
    ) {
        gateways = Map.of(
                "stripe", stripe,
                "razorpay", razorpay
        );
    }

    @Override
    public PaymentGateway getPaymentGateway(String name) {
        return Optional.ofNullable(gateways.get(name.toLowerCase()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid gateway"));
    }
}