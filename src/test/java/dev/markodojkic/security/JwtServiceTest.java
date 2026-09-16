package dev.markodojkic.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {
    @Test
    void shouldGenerateAndReadJwt() {
        String secret = "this-is-a-test-secret-with-more-than-32-chars";
        JwtService service = new JwtService(secret);

        String token = service.generate("marko");

        assertNotNull(token);
        assertEquals("marko", service.subject(token));
    }
}
