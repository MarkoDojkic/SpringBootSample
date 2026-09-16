package dev.markodojkic.persistence;

import dev.markodojkic.config.JasyptEncryptorHolder;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.time.Instant;

@Converter
public class EncryptedInstantConverter implements AttributeConverter<Instant, String> {
    @Override
    public String convertToDatabaseColumn(Instant attribute) {
        return attribute == null ? null : JasyptEncryptorHolder.get().encrypt(attribute.toString());
    }

    @Override
    public Instant convertToEntityAttribute(String column) {
        return column == null ? null : Instant.parse(JasyptEncryptorHolder.get().decrypt(column));
    }
}
