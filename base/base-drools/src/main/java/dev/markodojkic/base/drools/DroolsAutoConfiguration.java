package dev.markodojkic.base.drools;

import org.kie.api.KieServices;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnClass(KieServices.class)
public class DroolsAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    KieServices kieServices() {
        return KieServices.Factory.get();
    }

    @Bean
    @ConditionalOnMissingBean
    DroolsSessionFactory droolsSessionFactory(KieServices kieServices) {
        return new DroolsSessionFactory(kieServices);
    }
}
