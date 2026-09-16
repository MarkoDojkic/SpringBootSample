package dev.markodojkic.api;

import dev.markodojkic.messaging.EventPublisher;
import dev.markodojkic.messaging.KafkaEventPublisher;
import java.util.Map;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/integrations/events")
public class IntegrationEventController {
    private final EventPublisher rabbitPublisher;
    private final ObjectProvider<KafkaEventPublisher> kafkaPublisher;

    public IntegrationEventController(
            EventPublisher rabbitPublisher, ObjectProvider<KafkaEventPublisher> kafkaPublisher) {
        this.rabbitPublisher = rabbitPublisher;
        this.kafkaPublisher = kafkaPublisher;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Map<String, Boolean> publish(@RequestBody EventRequest request) {
        rabbitPublisher.publish(request.message());
        KafkaEventPublisher publisher = kafkaPublisher.getIfAvailable();
        if (publisher != null) {
            publisher.publish(request.message());
        }
        return Map.of("rabbitMq", true, "kafka", publisher != null);
    }

    public record EventRequest(String message) {
    }
}
