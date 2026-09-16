package dev.markodojkic.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            try {
                String subject = jwtService.subject(header.substring(7));
                var authentication = new UsernamePasswordAuthenticationToken(
                        subject, null, AuthorityUtils.createAuthorityList("ROLE_USER"));
                org.springframework.security.core.context.SecurityContextHolder
                        .getContext().setAuthentication(authentication);
            } catch (Exception ignored) {
                // Invalid tokens remain unauthenticated and are handled by Spring Security.
            }
        }
        filterChain.doFilter(request, response);
    }
}
