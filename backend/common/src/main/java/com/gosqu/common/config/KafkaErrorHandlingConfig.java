package com.gosqu.common.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.ExponentialBackOff;

/**
 * Rekordy nie do odzyskania po retry lądują na "{topic}.DLT" zamiast blokować partycję.
 * Docelowa partycja DLT jest zawsze wyznaczana na nowo (-1 = Kafka wybiera), bo
 * DeadLetterPublishingRecoverer domyślnie próbuje trafić w ten sam numer partycji co
 * rekord źródłowy, co wysypuje się, gdy temat źródłowy ma inną liczbę partycji niż jego DLT.
 */
@Configuration
public class KafkaErrorHandlingConfig {

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(KafkaTemplate<Object, Object> kafkaTemplate,
                                                   MeterRegistry meterRegistry) {
        DeadLetterPublishingRecoverer publishingRecoverer = new DeadLetterPublishingRecoverer(kafkaTemplate,
                (record, ex) -> new TopicPartition(record.topic() + ".DLT", -1));

        ExponentialBackOff backOff = new ExponentialBackOff(1000L, 2.0);
        backOff.setMaxElapsedTime(15_000L);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
                (record, ex) -> {
                    Counter.builder("kafka.consumer.dlt.count")
                            .tag("topic", record.topic())
                            .register(meterRegistry)
                            .increment();
                    publishingRecoverer.accept(record, ex);
                },
                backOff);
        errorHandler.addNotRetryableExceptions(IllegalArgumentException.class);
        return errorHandler;
    }
}
