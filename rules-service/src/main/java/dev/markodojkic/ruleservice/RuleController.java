package dev.markodojkic.ruleservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleController {
    private final RuleEvaluationService ruleEvaluationService;

    @GetMapping("/evaluate")
    public RuleResultDto evaluate(@RequestParam(name = "age") int age) {
        return ruleResultMapper.toDto(ruleEvaluationService.evaluate(age));
    }

    private final RuleResultMapper ruleResultMapper;
}
