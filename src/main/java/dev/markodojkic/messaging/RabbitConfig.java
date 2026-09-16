package dev.markodojkic.messaging;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String EXCHANGE = "enterprise.events";
    public static final String QUEUE = "enterprise.events.queue";
    public static final String ROUTING_KEY = "enterprise.event";

    @Bean TopicExchange enterpriseExchange() {
        return new TopicExchange(EXCHANGE);
    }

    @Bean Queue enterpriseQueue() {
        return QueueBuilder.durable(QUEUE).build();
    }

    @Bean Binding enterpriseBinding(Queue enterpriseQueue, TopicExchange enterpriseExchange) {
        return BindingBuilder.bind(enterpriseQueue).to(enterpriseExchange).with(ROUTING_KEY);
    }
}
