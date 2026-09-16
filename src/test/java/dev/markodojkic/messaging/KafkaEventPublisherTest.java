package dev.markodojkic.messaging;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

@ExtendWith(MockitoExtension.class)
class KafkaEventPublisherTest {
    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void shouldPublishEventToEnterpriseTopic() {
        new KafkaEventPublisher(kafkaTemplate).publish("hello");

        verify(kafkaTemplate).send(KafkaConfig.TOPIC, "hello");
    }
}
