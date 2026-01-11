package com.ecommerce.paymentservice.services.webhook;

import com.ecommerce.paymentservice.services.PaymentStatusProcessor;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayWebhookHandler {

    @Value("${payment.razorpay.webhook-secret}")
    private String secret;

    private final PaymentStatusProcessor processor;

    public RazorpayWebhookHandler(PaymentStatusProcessor processor) {
        this.processor = processor;
    }

    public void handle(String payload, String signature) throws Exception {

        boolean valid = Utils.verifyWebhookSignature(payload, signature, secret);
        if (!valid) return;

        JSONObject json = new JSONObject(payload);
        String event = json.getString("event");

        if ("payment.captured".equals(event)) {

            JSONObject payment = json
                    .getJSONObject("payload")
                    .getJSONObject("payment")
                    .getJSONObject("entity");

            String orderId = payment.getString("order_id");

            processor.markSuccessByGatewayPaymentId(orderId, "razorpay");
        }
    }
}