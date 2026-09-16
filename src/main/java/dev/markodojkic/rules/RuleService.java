package dev.markodojkic.rules;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

@Service
public class RuleService {
    public static final class RuleInput {
        private final int age;

        public RuleInput(int age) {
            this.age = age;
        }

        public int getAge() {
            return age;
        }
    }

    public record RuleResult(int age, String category) {}

    @Cacheable(cacheNames = "rule-evaluations", key = "#p0")
    public RuleResult evaluate(int age) {
        RuleInput input = new RuleInput(age);
        KieServices ks = KieServices.Factory.get();
        try (KieSession session = ks.newKieClasspathContainer().newKieSession("rulesSession")) {
            session.insert(input);
            session.fireAllRules();
            return new RuleResult(input.getAge(), category(input.getAge()));
        }
    }

    private String category(int age) {
        if (age < 18) return "MINOR";
        if (age < 65) return "ADULT";
        return "SENIOR";
    }
}
