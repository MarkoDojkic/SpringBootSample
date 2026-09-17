package dev.markodojkic.api;

import dev.markodojkic.model.IntegrationMessage;
import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("hikari")
public class HikariIntegrationMessageMapper implements MessageMapper {
    private final IntegrationMessageMapper delegate =
            Mappers.getMapper(IntegrationMessageMapper.class);

    @Override
    public IntegrationMessageDto toDto(IntegrationMessage message) {
        return delegate.toDto(message);
    }
}
