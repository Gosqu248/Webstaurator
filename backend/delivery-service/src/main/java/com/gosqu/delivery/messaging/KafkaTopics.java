package com.gosqu.delivery.messaging;

// Stałe zamiast rozsianych po konsumentach/producencie literałów stringowych — jedno miejsce
// do zmiany nazwy topicu, @KafkaListener(topics = ...) wymaga stałej ze stałą wartością znaną
// w czasie kompilacji, więc "public static final String" (nie enum) jest tu konieczne.
public final class KafkaTopics {

    public static final String ORDER_CONFIRMED = "order.confirmed";
    public static final String ORDER_PREPARED = "order.prepared";
    public static final String DELIVERY_LOCATION = "delivery.location";

    private KafkaTopics() {
    }
}
