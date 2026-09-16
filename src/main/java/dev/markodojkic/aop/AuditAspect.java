package dev.markodojkic.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {
    private static final Logger log = LoggerFactory.getLogger(AuditAspect.class);

    @Around("execution(* dev.markodojkic..*(..)) && @within(org.springframework.stereotype.Service)")
    public Object audit(ProceedingJoinPoint pjp) throws Throwable {
        long start = System.nanoTime();
        try {
            return pjp.proceed();
        } finally {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.info("AUDIT method={} durationMs={}", pjp.getSignature().toShortString(), elapsedMs);
        }
    }
}
