package dev.markodojkic.ruleservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rules")
public class RuleController {
    private final RuleEvaluationService ruleEvaluationService;

    public RuleController(RuleEvaluationService ruleEvaluationService) {
        this.ruleEvaluationService = ruleEvaluationService;
    }

    @GetMapping("/evaluate")
    public RuleEvaluationService.RuleResult evaluate(@RequestParam(name = "age") int age) {
        return ruleEvaluationService.evaluate(age);
    }
}
