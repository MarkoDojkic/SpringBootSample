package dev.markodojkic.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.time.Instant;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.hibernate.envers.Audited;
import jakarta.persistence.Convert;
import dev.markodojkic.persistence.EncryptedBytesConverter;
import dev.markodojkic.persistence.EncryptedInstantConverter;
import dev.markodojkic.persistence.EncryptedStringConverter;

@Entity
@Audited
@Table(name = "integration_message")
@EntityListeners(AuditingEntityListener.class)
public class IntegrationMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "integration-message-seq")
    @SequenceGenerator(
            name = "integration-message-seq",
            sequenceName = "integration_message_seq",
            allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 512)
    private String message;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Convert(converter = EncryptedStringConverter.class)
    @Lob
    @Column(name = "secret_text")
    private String secretText;

    @Convert(converter = EncryptedInstantConverter.class)
    @Lob
    @Column(name = "secret_date")
    private Instant secretDate;

    @Convert(converter = EncryptedBytesConverter.class)
    @Lob
    @Column(name = "secret_bytes")
    private byte[] secretBytes;

    protected IntegrationMessage() {
    }

    public IntegrationMessage(String message) {
        this.message = message;
    }

    public IntegrationMessage(String message, String secretText, Instant secretDate, byte[] secretBytes) {
        this.message = message;
        this.secretText = secretText;
        this.secretDate = secretDate;
        this.secretBytes = secretBytes;
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getSecretText() {
        return secretText;
    }

    public Instant getSecretDate() {
        return secretDate;
    }

    public byte[] getSecretBytes() {
        return secretBytes;
    }
}
