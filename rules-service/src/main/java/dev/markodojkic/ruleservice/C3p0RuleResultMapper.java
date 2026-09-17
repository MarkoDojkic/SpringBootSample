package dev.markodojkic.ruleservice;

import com.github.dozermapper.core.Mapper;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("c3p0")
public class C3p0RuleResultMapper implements RuleResultMapper {
    private final Mapper delegate =
            com.github.dozermapper.core.DozerBeanMapperBuilder.create()
                    .withMappingFiles("dozer/rule-result-mapping.xml")
                    .build();

    @Override
    public RuleResultDto toDto(RuleEvaluationService.RuleResult result) {
        return delegate.map(result, RuleResultDto.class);
    }
}
