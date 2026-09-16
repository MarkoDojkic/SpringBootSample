package dev.markodojkic.client;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RulesClientTest {
    @Mock
    RulesClient client;

    @Test
    void shouldUseFeignClientContract() {
        RulesClient.RuleResult result = new RulesClient.RuleResult(42, "ADULT");
        when(client.evaluate(42)).thenReturn(result);
        assertEquals(result, client.evaluate(42));
        verify(client).evaluate(42);
    }
}
