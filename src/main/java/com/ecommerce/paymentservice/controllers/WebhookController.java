package com.ecommerce.paymentservice.controllers;

import com.ecommerce.paymentservice.services.webhook.RazorpayWebhookHandler;
import com.ecommerce.paymentservice.services.webhook.StripeWebhookHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    private final StripeWebhookHandler stripeHandler;
    private final RazorpayWebhookHandler razorpayHandler;

    public WebhookController(StripeWebhookHandler stripeHandler, RazorpayWebhookHandler razorpayHandler) {
        this.stripeHandler = stripeHandler;
        this.razorpayHandler = razorpayHandler;
    }

    @PostMapping("/stripe")
    public ResponseEntity<Void> stripe(@RequestBody String payload,
                                 @RequestHeader(name = "Stripe-Signature", required = false) String sig)
            throws Exception {
        if(sig == null || sig.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        stripeHandler.handle(payload, sig);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/razorpay")
    public void razorpay(@RequestBody String payload,
                         @RequestHeader("X-Razorpay-Signature") String sig)
            throws Exception {
        razorpayHandler.handle(payload, sig);
    }
}

