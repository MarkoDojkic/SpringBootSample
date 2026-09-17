package dev.markodojkic.api;

import dev.markodojkic.model.IntegrationMessage;

public interface MessageMapper {
    IntegrationMessageDto toDto(IntegrationMessage message);
}
