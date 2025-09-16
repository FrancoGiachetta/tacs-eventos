package tacs.eventos.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tacs.eventos.service.SessionService;

import java.io.IOException;

@Component
public class SessionAuthFilter extends OncePerRequestFilter {
    private final SessionService sessions;

    public SessionAuthFilter(SessionService sessions) {
        this.sessions = sessions;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        System.out.println("Filter");
        String token = extractToken(request);
        if (token != null) {
            sessions.validate(token).ifPresentOrElse(u -> {
                var authorities = u.getRoles().stream().map(r -> new SimpleGrantedAuthority("ROLE_" + r.name()))
                        .toList();

                var auth = new UsernamePasswordAuthenticationToken(u, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }, () -> {
                System.out.println("Token inválido o expirado");
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            });
        }
        chain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest req) {
        String header = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith("Bearer "))
            return header.substring(7);
        String x = req.getHeader("X-Session-Token");
        return (x == null || x.isBlank()) ? null : x;
    }
}
