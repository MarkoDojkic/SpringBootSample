package dev.markodojkic.api;

import dev.markodojkic.client.RulesClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rules")
public class RuleController {
    private final RulesClient rulesClient;

    public RuleController(RulesClient rulesClient) {
        this.rulesClient = rulesClient;
    }

    @GetMapping("/evaluate")
    public RulesClient.RuleResult evaluate(@RequestParam(name = "age") int age) {
        return rulesClient.evaluate(age);
    }
}
