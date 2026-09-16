package dev.markodojkic.config;

import org.jasypt.encryption.StringEncryptor;

public final class JasyptEncryptorHolder {
    private static volatile StringEncryptor encryptor;

    private JasyptEncryptorHolder() {
    }

    public static void initialize(StringEncryptor value) {
        encryptor = value;
    }

    public static StringEncryptor get() {
        StringEncryptor value = encryptor;
        if (value == null) {
            throw new IllegalStateException("Jasypt encryptor has not been initialized");
        }
        return value;
    }
}
