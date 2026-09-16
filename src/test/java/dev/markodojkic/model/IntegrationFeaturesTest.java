package dev.markodojkic.model;

import static org.junit.jupiter.api.Assertions.assertTrue;

import jakarta.persistence.EntityListeners;
import org.hibernate.envers.Audited;
import org.hibernate.envers.RevisionEntity;
import org.junit.jupiter.api.Test;

class IntegrationFeaturesTest {
    @Test
    void shouldEnableAuditingAndEnversOnMessageEntity() {
        assertTrue(IntegrationMessage.class.isAnnotationPresent(Audited.class));
        assertTrue(IntegrationMessage.class.isAnnotationPresent(EntityListeners.class));
        assertTrue(RevInfo.class.isAnnotationPresent(RevisionEntity.class));
    }
}
