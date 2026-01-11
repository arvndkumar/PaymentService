package com.ecommerce.paymentservice.services;

import com.ecommerce.paymentservice.dtos.PaymentReceiptResponse;
import com.ecommerce.paymentservice.dtos.PaymentRequest;
import com.ecommerce.paymentservice.dtos.PaymentResponse;

public interface PaymentService
{
    PaymentResponse initiatePayment(PaymentRequest request) throws Exception;
    PaymentReceiptResponse getReceipt(String orderId);

}

