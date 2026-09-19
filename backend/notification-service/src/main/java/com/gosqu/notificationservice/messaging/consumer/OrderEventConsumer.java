package com.gosqu.notificationservice.messaging.consumer;

import com.gosqu.notificationservice.client.UserClient;
import com.gosqu.notificationservice.email.EmailService;
import com.gosqu.notificationservice.messaging.consumer.dto.*;
import com.gosqu.notificationservice.notification.Notification;
import com.gosqu.notificationservice.notification.NotificationRepository;
import com.gosqu.notificationservice.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final EmailService emailService;
    private final NotificationRepository notificationRepository;
    private final UserClient userClient;

    @KafkaListener(topics = "order.created", groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderCreated(OrderCreatedEvent event) {
        sendAndLog(event.customerId(), event.orderId(),
                "Zamówienie przyjęte", "Twoje zamówienie #" + event.orderId() + " zostało przyjęte.");
    }

    @KafkaListener(topics = "order.confirmed", groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderConfirmed(OrderConfirmedEvent event) {
        sendAndLog(event.customerId(), event.orderId(),
                "Zamówienie potwierdzone", "Twoje zamówienie #" + event.orderId() + " zostało potwierdzone.");
    }

    @KafkaListener(topics = "order.prepared", groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderPrepared(OrderPreparedEvent event) {
        sendAndLog(event.customerId(), event.orderId(),
                "Zamówienie przygotowane", "Twoje zamówienie #" + event.orderId() + " zostało przygotowane.");
    }

    @KafkaListener(topics = "order.picked-up", groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderPickedUp(OrderPickedUpEvent event) {
        sendAndLog(event.customerId(), event.orderId(),
                "Zamówienie w drodze", "Twoje zamówienie #" + event.orderId() + " jest w drodze.");
    }

    @KafkaListener(topics = "order.delivered", groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderDelivered(OrderDeliveredEvent event) {
        sendAndLog(event.customerId(), event.orderId(),
                "Zamówienie dostarczone", "Twoje zamówienie #" + event.orderId() + " zostało dostarczone.");
    }

    @KafkaListener(topics = "order.cancelled", groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderCancelled(OrderCancelledEvent event) {
        sendAndLog(event.customerId(), event.orderId(),
                "Zamówienie anulowane", "Twoje zamówienie #" + event.orderId() + " zostało anulowane.");
    }

    private void sendAndLog(UUID customerId, UUID orderId, String subject, String body) {
        try {
            String email = userClient.getEmail(customerId);
            emailService.send(email, subject, body);
            notificationRepository.save(Notification.sent(customerId, NotificationType.EMAIL, subject, body, orderId));

        } catch (Exception e) {
            log.error("action=send_notification_failed orderId={}", orderId, e);
            notificationRepository.save(Notification.failed(customerId, NotificationType.EMAIL, subject, body, orderId));
        }
    }
}
