package com.ecommerce.paymentservice.services;

import com.ecommerce.paymentservice.messaging.event.PaymentStatusEvent;
import com.ecommerce.paymentservice.messaging.producer.PaymentEventProducer;
import com.ecommerce.paymentservice.model.PaymentStatus;
import com.ecommerce.paymentservice.model.PaymentTransaction;
import com.ecommerce.paymentservice.repositories.PaymentTransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class PaymentStatusProcessor {

    private final PaymentTransactionRepository repo;
    private final PaymentEventProducer producer;


    public PaymentStatusProcessor(PaymentTransactionRepository repo, PaymentEventProducer producer) {
        this.repo = repo;
        this.producer = producer;
    }


    @Transactional
    public void markSuccessByOrderId(String orderId, String gatewayPaymentId, String gateway) {
        PaymentTransaction txn = repo.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("No payment transaction found for orderId=" + orderId));

        if (txn.getGatewayPaymentId() == null || txn.getGatewayPaymentId().isBlank()) {

            txn.setGatewayPaymentId(gatewayPaymentId);
        }
        markSuccess(gateway, txn);
    }


    @Transactional
    public void markSuccessByGatewayPaymentId(String gatewayPaymentId, String gateway) {
        PaymentTransaction txn = repo.findByGatewayPaymentId(gatewayPaymentId)
                .orElseThrow(() -> new IllegalArgumentException("No payment transaction found for gatewayPaymentId=" + gatewayPaymentId));


        markSuccess(gateway, txn);
    }

    private void markSuccess(String gateway, PaymentTransaction txn) {
        txn.setGateway(gateway);
        txn.setStatus(PaymentStatus.SUCCESS);
        txn.setUpdatedAt(Instant.now());

        repo.save(txn);

        PaymentStatusEvent event = PaymentStatusEvent.builder()
                .orderId(txn.getOrderId())
                .transactionId(txn.getId())
                .gatewayPaymentId(txn.getGatewayPaymentId())
                .status("SUCCESS")
                .gateway(txn.getGateway())
                .amountMinor(txn.getAmountMinor())
                .currency(txn.getCurrency())
                .eventAt(Instant.now())
                .build();

        producer.publish(event);

    }

    @Transactional
    public void markFailedByGatewayPaymentId(String gatewayPaymentId, String gateway) {

        PaymentTransaction txn = repo.findByGatewayPaymentId(gatewayPaymentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No payment transaction found for gatewayPaymentId=" + gatewayPaymentId));

        markFailed(gateway, txn);
    }

    private void markFailed(String gateway, PaymentTransaction txn) {
        if(txn.getStatus() == PaymentStatus.SUCCESS) {
            return;
        }

        if (txn.getStatus() == PaymentStatus.FAILED) {
            return;
        }

        txn.setGateway(gateway);
        txn.setStatus(PaymentStatus.FAILED);
        txn.setUpdatedAt(Instant.now());
        repo.save(txn);

        PaymentStatusEvent event = PaymentStatusEvent.builder()
                .orderId(txn.getOrderId())
                .transactionId(txn.getId())
                .gatewayPaymentId(txn.getGatewayPaymentId())
                .status("FAILED")
                .gateway(txn.getGateway())
                .amountMinor(txn.getAmountMinor())
                .currency(txn.getCurrency())
                .eventAt(Instant.now())
                .build();

        producer.publish(event);
    }
}
