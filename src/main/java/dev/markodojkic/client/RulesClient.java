package dev.markodojkic.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "rules-service")
public interface RulesClient {
    @GetMapping("/api/rules/evaluate")
    RuleResult evaluate(@RequestParam(name = "age") int age);

    record RuleResult(int age, String category) {
    }
}
