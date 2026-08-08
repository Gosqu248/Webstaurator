package com.gosqu.notificationservice.notification.dto.response;

import com.gosqu.notificationservice.notification.Notification;
import com.gosqu.notificationservice.notification.NotificationStatus;
import com.gosqu.notificationservice.notification.NotificationType;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        String id,
        NotificationType type,
        String title,
        String body,
        UUID orderId,
        NotificationStatus status,
        Instant sentAt
) {

    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(
                notification.id(),
                notification.type(),
                notification.title(),
                notification.body(),
                notification.orderId(),
                notification.status(),
                notification.sentAt()
        );
    }
}
