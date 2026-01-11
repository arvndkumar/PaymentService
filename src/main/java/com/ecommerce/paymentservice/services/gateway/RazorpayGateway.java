package com.ecommerce.paymentservice.services.gateway;

import com.ecommerce.paymentservice.dtos.PaymentRequest;
import com.ecommerce.paymentservice.dtos.PaymentResponse;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RazorpayGateway implements PaymentGateway {

    @Value("${payment.razorpay.key-id}")
    private String keyId;

    @Value("${payment.razorpay.key-secret}")
    private String keySecret;


    @Override
    public PaymentResponse createPayment(PaymentRequest request) throws Exception {

        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        long amountMinor = BigDecimal.valueOf(request.getAmount())
                .setScale(2, RoundingMode.HALF_UP)
                .movePointRight(2)
                .longValueExact();

        JSONObject options = new JSONObject();
        options.put("amount", amountMinor);
        options.put("currency", request.getCurrency());
        options.put("receipt", request.getOrderId());

        Order order = client.orders.create(options);

        return new PaymentResponse(
                order.get("id"),
                null,
                "razorpay"
        );

    }
}
