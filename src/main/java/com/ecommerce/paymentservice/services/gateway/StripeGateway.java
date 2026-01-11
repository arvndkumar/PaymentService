package com.ecommerce.paymentservice.services.gateway;

import com.ecommerce.paymentservice.dtos.PaymentRequest;
import com.ecommerce.paymentservice.dtos.PaymentResponse;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Primary
public class StripeGateway implements PaymentGateway {

    @Value("${payment.stripe.secret-key}")
    private String stripeSecretKey;

    @PostConstruct
    void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public PaymentResponse createPayment(PaymentRequest request) throws Exception {

        Map<String, Object> params = new HashMap<>();
        long amountMinor = BigDecimal.valueOf(request.getAmount())
                .setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2)
                .longValueExact();
        params.put("amount", amountMinor);
        params.put("currency", request.getCurrency());
        params.put("payment_method_types", List.of("card"));
        params.put("metadata", Map.of("orderId", request.getOrderId()));

        PaymentIntent intent = PaymentIntent.create(params);

        return new PaymentResponse(
                intent.getId(),
                intent.getClientSecret(),
                "stripe"
        );
    }
}
