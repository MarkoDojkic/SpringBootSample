package dev.markodojkic.persistence;

import dev.markodojkic.config.JasyptEncryptorHolder;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

@Converter
public class EncryptedBytesConverter implements AttributeConverter<byte[], byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(byte[] attribute) {
        return attribute == null ? null
                : JasyptEncryptorHolder.get()
                        .encrypt(Base64.getEncoder().encodeToString(attribute))
                        .getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public byte[] convertToEntityAttribute(byte[] column) {
        return column == null
                ? null
                : Base64.getDecoder().decode(JasyptEncryptorHolder.get()
                        .decrypt(new String(column, StandardCharsets.UTF_8)));
    }
}
