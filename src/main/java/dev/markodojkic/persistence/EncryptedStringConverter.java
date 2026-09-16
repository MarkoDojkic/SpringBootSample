package dev.markodojkic.persistence;

import dev.markodojkic.config.JasyptEncryptorHolder;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class EncryptedStringConverter implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String attribute) {
        return attribute == null ? null : JasyptEncryptorHolder.get().encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String column) {
        return column == null ? null : JasyptEncryptorHolder.get().decrypt(column);
    }
}
