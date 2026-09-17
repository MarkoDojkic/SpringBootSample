package dev.markodojkic.ruleservice;

import dev.markodojkic.base.drools.DroolsSessionFactory;
import org.kie.api.runtime.KieSession;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RuleEvaluationService {
    private final DroolsSessionFactory sessionFactory;

    @lombok.Getter
    public static final class RuleInput {
        private final int age;

        public RuleInput(int age) {
            this.age = age;
        }
    }

    @lombok.Data
    @lombok.AllArgsConstructor
    public static final class RuleResult {
        private final int age;
        private final String category;
    }

    @Cacheable(cacheNames = "rule-evaluations", key = "#p0")
    public RuleResult evaluate(int age) {
        RuleInput input = new RuleInput(age);
        try (KieSession session = sessionFactory.create("rulesSession")) {
            session.insert(input);
            session.fireAllRules();
            return new RuleResult(input.getAge(), category(input.getAge()));
        }
    }

    private String category(int age) {
        if (age < 18) {
            return "MINOR";
        }
        if (age < 65) {
            return "ADULT";
        }
        return "SENIOR";
    }
}
