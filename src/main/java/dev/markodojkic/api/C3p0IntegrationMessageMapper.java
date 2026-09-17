package dev.markodojkic.api;

import com.github.dozermapper.core.Mapper;
import dev.markodojkic.model.IntegrationMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("c3p0")
public class C3p0IntegrationMessageMapper implements MessageMapper {
    private final Mapper delegate;

    public C3p0IntegrationMessageMapper() {
        this.delegate = com.github.dozermapper.core.DozerBeanMapperBuilder.create()
                .withMappingFiles("dozer/integration-message-mapping.xml")
                .build();
    }

    @Override
    public IntegrationMessageDto toDto(IntegrationMessage message) {
        return delegate.map(message, IntegrationMessageDto.class);
    }
}
