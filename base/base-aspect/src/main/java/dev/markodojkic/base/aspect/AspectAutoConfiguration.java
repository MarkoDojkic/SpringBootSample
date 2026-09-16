package dev.markodojkic.base.aspect;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.aspectj.lang.annotation.Aspect;

@AutoConfiguration
@AutoConfigureBefore(AopAutoConfiguration.class)
@ConditionalOnClass(Aspect.class)
@ConditionalOnMissingBean(AbstractAutoProxyCreator.class)
@EnableAspectJAutoProxy
public class AspectAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    AuditAspect auditAspect() {
        return new AuditAspect();
    }
}
