package com.gosqu.notificationservice.notification;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "notifications")
public record Notification(
        @Id
        String id,
        UUID userId,
        NotificationType type,
        String title,
        String body,
        UUID orderId,
        NotificationStatus status,
        Instant sentAt
) {
    public static Notification sent(UUID userId, NotificationType type, String title, String body, UUID orderId) {
        return new Notification(null, userId, type, title, body, orderId, NotificationStatus.SENT, Instant.now());
    }

    public static Notification failed(UUID userId, NotificationType type, String title, String body, UUID orderId) {
        return new Notification(null, userId, type, title, body, orderId, NotificationStatus.FAILED, Instant.now());
    }
}
