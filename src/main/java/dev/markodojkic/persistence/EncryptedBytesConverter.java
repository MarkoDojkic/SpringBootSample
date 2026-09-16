package dev.markodojkic.persistence;

import dev.markodojkic.config.JasyptEncryptorHolder;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.Base64;

@Converter
public class EncryptedBytesConverter implements AttributeConverter<byte[], String> {
    @Override
    public String convertToDatabaseColumn(byte[] attribute) {
        return attribute == null ? null
                : JasyptEncryptorHolder.get().encrypt(Base64.getEncoder().encodeToString(attribute));
    }

    @Override
    public byte[] convertToEntityAttribute(String column) {
        return column == null
                ? null
                : Base64.getDecoder().decode(JasyptEncryptorHolder.get().decrypt(column));
    }
}
