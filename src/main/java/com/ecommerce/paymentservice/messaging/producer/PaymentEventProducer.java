package com.ecommerce.paymentservice.messaging.producer;

import com.ecommerce.paymentservice.messaging.event.PaymentStatusEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentEventProducer {
    private final KafkaTemplate<String, PaymentStatusEvent> kafkaTemplate;

    @Value("${payment.kafka.topic:payment-status-topic}")
    private String topic;

    public PaymentEventProducer(KafkaTemplate<String, PaymentStatusEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void publish(PaymentStatusEvent event) {
        kafkaTemplate.send(topic, event.getOrderId(), event);
    }
}
