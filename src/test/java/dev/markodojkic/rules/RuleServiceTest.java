package dev.markodojkic.rules;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RuleServiceTest {
    @Test
    void shouldClassifyAdults() {
        RuleService service = new RuleService();
        var result = service.evaluate(35);
        assertEquals("ADULT", result.category());
    }
}
