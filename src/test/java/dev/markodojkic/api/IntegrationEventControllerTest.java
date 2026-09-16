package dev.markodojkic.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.markodojkic.messaging.EventPublisher;
import dev.markodojkic.messaging.KafkaEventPublisher;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.ObjectProvider;

class IntegrationEventControllerTest {
    @Test
    void shouldPublishToRabbitAndKafkaWhenKafkaIsEnabled() {
        EventPublisher rabbit = Mockito.mock(EventPublisher.class);
        KafkaEventPublisher kafka = Mockito.mock(KafkaEventPublisher.class);
        ObjectProvider<KafkaEventPublisher> provider = Mockito.mock(ObjectProvider.class);
        when(provider.getIfAvailable()).thenReturn(kafka);

        Map<String, Boolean> result =
                new IntegrationEventController(rabbit, provider)
                        .publish(new IntegrationEventController.EventRequest("hello"));

        verify(rabbit).publish("hello");
        verify(kafka).publish("hello");
        assertEquals(Map.of("rabbitMq", true, "kafka", true), result);
    }
}
