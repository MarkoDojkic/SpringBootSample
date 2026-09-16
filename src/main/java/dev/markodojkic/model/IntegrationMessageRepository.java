package dev.markodojkic.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface IntegrationMessageRepository extends JpaRepository<IntegrationMessage, Long> {
}
