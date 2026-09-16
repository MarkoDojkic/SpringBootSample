package dev.markodojkic.api;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FhirControllerTest {
    @Test
    void shouldSerializePatientAsFhirJson() {
        String json = new FhirController().patient("Doe", "Jane");
        assertTrue(json.contains("\"resourceType\": \"Patient\""));
        assertTrue(json.contains("\"family\": \"Doe\""));
        assertTrue(json.contains("\"Jane\""));
    }
}
