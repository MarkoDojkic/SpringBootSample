package dev.markodojkic.api;

import dev.markodojkic.security.JwtService;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtService jwtService;

    public AuthController(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @PostMapping("/token")
    public Map<String, String> token(@RequestParam(defaultValue = "demo-user") String username) {
        return Map.of("token", jwtService.generate(username));
    }
}
