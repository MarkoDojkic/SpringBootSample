package dev.markodojkic.messaging;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventPublisherTest {
    @Mock
    RabbitTemplate rabbitTemplate;

    @Test
    void shouldPublishEvent() {
        EventPublisher publisher = new EventPublisher(rabbitTemplate);
        publisher.publish("hello");

        verify(rabbitTemplate).convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                "hello");
    }
}
