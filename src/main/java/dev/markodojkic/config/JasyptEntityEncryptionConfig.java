package dev.markodojkic.config;

import jakarta.annotation.PostConstruct;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptEntityEncryptionConfig {
    private final StringEncryptor encryptor;

    public JasyptEntityEncryptionConfig(StringEncryptor encryptor) {
        this.encryptor = encryptor;
    }

    @PostConstruct
    void initialize() {
        JasyptEncryptorHolder.initialize(encryptor);
    }
}
