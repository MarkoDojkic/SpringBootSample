package dev.markodojkic.ruleservice;

import org.mapstruct.Mapper;

@Mapper
public interface RuleResultMapper {
    RuleResultDto toDto(RuleEvaluationService.RuleResult result);
}
