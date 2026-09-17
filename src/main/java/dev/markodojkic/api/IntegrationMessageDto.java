package dev.markodojkic.api;

import java.time.Instant;
import lombok.Data;

@Data
public class IntegrationMessageDto {
    private Long id;
    private String message;
    private Instant createdAt;
    private Instant updatedAt;
    private String secretText;
    private Instant secretDate;
    private byte[] secretBytes;
}
