package dev.markodojkic.ruleservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rules")
public class RuleController {
    private final RuleEvaluationService ruleEvaluationService;
    private final RuleResultMapper ruleResultMapper;

    public RuleController(
            RuleEvaluationService ruleEvaluationService,
            RuleResultMapper ruleResultMapper) {
        this.ruleEvaluationService = ruleEvaluationService;
        this.ruleResultMapper = ruleResultMapper;
    }

    @GetMapping("/evaluate")
    public RuleResultDto evaluate(@RequestParam(name = "age") int age) {
        return ruleResultMapper.toDto(ruleEvaluationService.evaluate(age));
    }
}
