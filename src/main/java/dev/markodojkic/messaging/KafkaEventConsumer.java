package dev.markodojkic.messaging;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.messaging.kafka-enabled", havingValue = "true")
public class KafkaEventConsumer {
    @KafkaListener(topics = KafkaConfig.TOPIC)
    public void consume(String payload) {
        System.out.println("Kafka event received: " + payload);
    }
}
