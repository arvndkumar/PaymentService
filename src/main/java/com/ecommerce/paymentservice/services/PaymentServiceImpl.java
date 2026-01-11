package com.ecommerce.paymentservice.services;

import com.ecommerce.paymentservice.dtos.PaymentReceiptResponse;
import com.ecommerce.paymentservice.dtos.PaymentRequest;
import com.ecommerce.paymentservice.dtos.PaymentResponse;
import com.ecommerce.paymentservice.model.PaymentStatus;
import com.ecommerce.paymentservice.model.PaymentTransaction;
import com.ecommerce.paymentservice.repositories.PaymentTransactionRepository;
import com.ecommerce.paymentservice.services.gateway.PaymentGatewaySelector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentGatewaySelector selector;
    private final PaymentTransactionRepository repo;

    public PaymentServiceImpl(PaymentGatewaySelector selector,
                              PaymentTransactionRepository repo)
    {
        this.selector = selector;
        this.repo = repo;
    }

    @Override
    public PaymentResponse initiatePayment(PaymentRequest request) throws Exception {

        long amountMinor = toMinorUnits(request.getAmount());

        PaymentTransaction existing = repo.findByOrderId(request.getOrderId()).orElse(null);
        if (existing != null) return new PaymentResponse(existing.getGatewayPaymentId(), null, existing.getGateway());

        PaymentTransaction txn = PaymentTransaction.builder()
                .orderId(request.getOrderId())
                .gateway(normalizedGateway(request.getGateway()))
                .amountMinor(amountMinor)
                .currency(request.getCurrency().toUpperCase())
                .status(PaymentStatus.INITIATED)
                .receiptNumber("RCT-"+request.getOrderId())
                .build();

        repo.save(txn);

        PaymentResponse response = selector
                .getPaymentGateway(txn.getGateway())
                .createPayment(request);

        txn.setGatewayPaymentId(response.getPaymentId());
        txn.setStatus(PaymentStatus.PENDING);
        repo.save(txn);

        response.setGateway(txn.getGateway());
        return response;
    }

    private static long toMinorUnits(Double major) {
        BigDecimal bd = BigDecimal.valueOf(major).setScale(2, RoundingMode.HALF_UP);
        return bd.movePointRight(2).longValueExact();
    }

    private static String normalizedGateway(String gateway) {
        return gateway == null ? null : gateway.trim().toUpperCase();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentReceiptResponse getReceipt(String orderId) {
        PaymentTransaction txn = repo.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("No payment transaction found for orderId=" + orderId));


        return PaymentReceiptResponse.builder()
                .receiptNumber(txn.getReceiptNumber())
                .transactionId(txn.getId())
                .orderId(txn.getOrderId())
                .gateway(txn.getGateway())
                .gatewayPaymentId(txn.getGatewayPaymentId())
                .amountMinor(txn.getAmountMinor())
                .currency(txn.getCurrency())
                .status(txn.getStatus())
                .paidAt(txn.getStatus() == PaymentStatus.SUCCESS ? txn.getUpdatedAt() : null)
                .build();
    }
}
