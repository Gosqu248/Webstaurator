package com.gosqu.payment.messaging.producer;

public final class KafkaTopics {

    public static final String ORDER_CREATED   = "order.created";
    public static final String ORDER_CANCELLED = "order.cancelled";

    public static final String PAYMENT_COMPLETED = "payment.completed";
    public static final String PAYMENT_FAILED    = "payment.failed";

    private KafkaTopics() {}
}
