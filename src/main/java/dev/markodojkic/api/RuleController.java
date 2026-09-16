package dev.markodojkic.api;

import dev.markodojkic.rules.RuleService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rules")
public class RuleController {
    private final RuleService ruleService;

    public RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @GetMapping("/evaluate")
    public RuleService.RuleResult evaluate(@RequestParam int age) {
        return ruleService.evaluate(age);
    }
}
