package com.gosqu.notificationservice.messaging.consumer;

import com.gosqu.notificationservice.client.UserClient;
import com.gosqu.notificationservice.email.EmailService;
import com.gosqu.notificationservice.messaging.consumer.dto.PaymentFailedEvent;
import com.gosqu.notificationservice.notification.Notification;
import com.gosqu.notificationservice.notification.NotificationRepository;
import com.gosqu.notificationservice.notification.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final EmailService emailService;
    private final NotificationRepository notificationRepository;
    private final UserClient userClient;

    @KafkaListener(topics = "payment.failed", groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentFailed(PaymentFailedEvent event) {
        String subject = "Płatność nie powiodła się";
        String body = "Płatność za zamówienie #" + event.orderId() + " nie powiodła się: " + event.reason();
        try {
            String email = userClient.getEmail(event.customerId());
            emailService.send(email, subject, body);
            notificationRepository.save(Notification.sent(event.customerId(), NotificationType.EMAIL, subject, body, event.orderId()));

        } catch (Exception e) {
            log.error("action=send_notification_failed orderId={}", event.orderId(), e);
            notificationRepository.save(Notification.failed(event.customerId(), NotificationType.EMAIL, subject, body, event.orderId()));
        }
    }
}
