package com.ecommerce.paymentservice.services.webhook;

import com.ecommerce.paymentservice.services.PaymentStatusProcessor;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeWebhookHandler {

    private final PaymentStatusProcessor paymentStatusProcessor;
    @Value("${payment.stripe.webhook-secret}")
    private String webhookSecret;

    public StripeWebhookHandler(PaymentStatusProcessor paymentStatusProcessor) {
         this.paymentStatusProcessor = paymentStatusProcessor;
    }

    public void handle(String payload, String signature) throws Exception {
        Event event = Webhook.constructEvent(payload, signature, webhookSecret);

        PaymentIntent intent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject()
                .orElseThrow(() -> new IllegalArgumentException("Stripe PaymentIntent missing"));

        String piId = intent.getId();
        String orderId = intent.getMetadata() != null ? intent.getMetadata().get("orderId") : null;

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                if (orderId != null && !orderId.isBlank()) {
                    paymentStatusProcessor.markSuccessByOrderId(orderId, piId, "STRIPE");
                } else {
                    paymentStatusProcessor.markSuccessByGatewayPaymentId(piId, "STRIPE");
                }
            }
            case "payment_intent.payment_failed" -> {
                paymentStatusProcessor.markFailedByGatewayPaymentId(piId, "STRIPE");
            }
            default -> {  }
        }
    }

    }