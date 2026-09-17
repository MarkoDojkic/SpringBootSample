package dev.markodojkic.api;

import dev.markodojkic.model.IntegrationMessage;
import org.mapstruct.Mapper;

@Mapper
public interface IntegrationMessageMapper {
    IntegrationMessageDto toDto(IntegrationMessage message);
}
