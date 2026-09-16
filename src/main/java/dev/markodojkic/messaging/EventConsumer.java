package dev.markodojkic.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class EventConsumer {
    @RabbitListener(queues = RabbitConfig.QUEUE)
    public void consume(String payload) {
        System.out.println("RabbitMQ event received: " + payload);
    }
}
