package dev.markodojkic.ruleservice;

import org.mapstruct.factory.Mappers;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("hikari")
public class HikariRuleResultMapper implements RuleResultMapper {
    private final RuleResultMapper delegate = Mappers.getMapper(RuleResultMapper.class);

    @Override
    public RuleResultDto toDto(RuleEvaluationService.RuleResult result) {
        return delegate.toDto(result);
    }
}
