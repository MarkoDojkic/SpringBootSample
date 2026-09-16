package dev.markodojkic.api;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class ReportControllerTest {
    @Test
    void shouldGeneratePdfReport() throws Exception {
        var response = new ReportController().eligibility("P123");

        assertEquals(MediaType.APPLICATION_PDF, response.getHeaders().getContentType());
        assertEquals("%PDF", new String(response.getBody(), 0, 4));
        assertArrayEquals(new byte[] {'%', 'P', 'D', 'F'}, java.util.Arrays.copyOf(response.getBody(), 4));
    }
}
