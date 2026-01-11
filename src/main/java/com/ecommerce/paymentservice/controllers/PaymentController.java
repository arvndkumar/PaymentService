package com.ecommerce.paymentservice.controllers;

import com.ecommerce.paymentservice.dtos.PaymentReceiptResponse;
import com.ecommerce.paymentservice.dtos.PaymentRequest;
import com.ecommerce.paymentservice.dtos.PaymentResponse;
import com.ecommerce.paymentservice.services.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {


    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public PaymentResponse initiatePayment(
            @RequestBody PaymentRequest request
    ) throws Exception {
        return paymentService.initiatePayment(request);
    }

    @GetMapping("/{orderId}/receipt")
    public ResponseEntity<PaymentReceiptResponse> getReceipt(@PathVariable String orderId) {
        return ResponseEntity.ok(paymentService.getReceipt(orderId));
    }
}