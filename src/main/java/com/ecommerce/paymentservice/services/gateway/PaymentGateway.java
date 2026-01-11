package com.ecommerce.paymentservice.services.gateway;

import com.ecommerce.paymentservice.dtos.PaymentRequest;
import com.ecommerce.paymentservice.dtos.PaymentResponse;


public interface PaymentGateway
{

    PaymentResponse createPayment(PaymentRequest request) throws Exception;

}
