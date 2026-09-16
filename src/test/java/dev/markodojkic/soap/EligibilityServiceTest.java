package dev.markodojkic.soap;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EligibilityServiceTest {
    @Test
    void shouldReturnEligibility() {
        EligibilityService service = new EligibilityServiceImpl();
        assertEquals("ELIGIBLE:P123", service.checkEligibility("P123"));
    }
}
