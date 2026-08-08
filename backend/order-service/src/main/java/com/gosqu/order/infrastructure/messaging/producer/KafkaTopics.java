package com.gosqu.order.infrastructure.messaging.producer;

public final class KafkaTopics {

    public static final String ORDER_CREATED   = "order.created";
    public static final String ORDER_CONFIRMED  = "order.confirmed";
    public static final String ORDER_PREPARED   = "order.prepared";
    public static final String ORDER_PICKED_UP  = "order.picked-up";
    public static final String ORDER_DELIVERED  = "order.delivered";
    public static final String ORDER_CANCELLED  = "order.cancelled";

    public static final String PAYMENT_COMPLETED = "payment.completed";
    public static final String PAYMENT_FAILED    = "payment.failed";

    private KafkaTopics() {}
}
