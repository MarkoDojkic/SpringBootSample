package dev.markodojkic.model;

import org.springframework.data.jpa.domain.Specification;

public final class IntegrationMessageSpecifications {
    private IntegrationMessageSpecifications() {
    }

    public static Specification<IntegrationMessage> messageContains(String text) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("message")),
                        "%" + text.toLowerCase() + "%");
    }
}
