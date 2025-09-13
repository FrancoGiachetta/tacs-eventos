package tacs.eventos.auth;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;
import tacs.eventos.service.SessionService;

@SpringBootTest
public class SessionAuthFilterTest {

    private SessionService sessions;
    private SessionAuthFilter filter;

    @BeforeEach
    void setUp() {
        this.sessions = mock(SessionService.class);
        this.filter = new SessionAuthFilter(this.sessions);
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void conBearerValido_seteaAuthenticationYContinuaCadena() throws ServletException, IOException {
        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);

        when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer tok-123");
        var usuario = new Usuario("User@Mail.com", "hash", Set.of(RolUsuario.USUARIO));
        when(sessions.validate("tok-123")).thenReturn(Optional.of(usuario));

        filter.doFilterInternal(req, res, chain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertSame(usuario, auth.getPrincipal());
        assertEquals("user@mail.com", ((Usuario) auth.getPrincipal()).getEmail());
        // Verificar rol mapeado
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USUARIO")));
        verify(chain).doFilter(req, res);
    }

    @Test
    void conXSessionTokenValido_seteaAuthenticationYContinuaCadena() throws ServletException, IOException {
        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);

        when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(req.getHeader("X-Session-Token")).thenReturn("abc-xyz");
        var usuario = new Usuario("admin@mail.com", "hash", Set.of(RolUsuario.ADMIN));
        when(sessions.validate("abc-xyz")).thenReturn(Optional.of(usuario));

        filter.doFilterInternal(req, res, chain);

        var auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertSame(usuario, auth.getPrincipal());
        assertEquals("admin@mail.com", ((Usuario) auth.getPrincipal()).getEmail());
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        verify(chain).doFilter(req, res);
    }

    @Test
    void tokenInvalido_noSeteaAuthenticationYContinuaCadena() throws ServletException, IOException {
        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);

        when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer invalido");
        when(sessions.validate("invalido")).thenReturn(Optional.empty());

        filter.doFilterInternal(req, res, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(req, res);
    }

    @Test
    void sinToken_noSeteaAuthenticationYContinuaCadena() throws ServletException, IOException {
        var req = mock(HttpServletRequest.class);
        var res = mock(HttpServletResponse.class);
        var chain = mock(FilterChain.class);

        when(req.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        when(req.getHeader("X-Session-Token")).thenReturn(null);

        filter.doFilterInternal(req, res, chain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(chain).doFilter(req, res);
    }
}
