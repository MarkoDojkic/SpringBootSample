package dev.markodojkic.base.audit;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@AutoConfiguration
@ConditionalOnClass({EnableJpaAuditing.class, AuditingEntityListener.class})
@ConditionalOnProperty(name = "base.audit.jpa.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnMissingBean(name = "jpaAuditingHandler")
@EnableJpaAuditing
public class AuditAutoConfiguration {
}
