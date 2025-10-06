package tacs.eventos.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import tacs.eventos.config.TestMongoConfiguration;
import tacs.eventos.config.TestRedisConfiguration;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Session;
import tacs.eventos.model.Usuario;
import tacs.eventos.repository.sesion.SessionRepository;
import tacs.eventos.repository.usuario.UsuarioRepository;
import tacs.eventos.service.SessionService;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import({ TestRedisConfiguration.class, TestMongoConfiguration.class })
@ActiveProfiles("test")
@Testcontainers
class SessionServiceTest {

    private UsuarioRepository usuarios;
    private SessionRepository sesiones;
    private PasswordEncoder encoder;
    private SessionService service;

    @BeforeEach
    void setUp() {
        this.usuarios = mock(UsuarioRepository.class);
        this.sesiones = mock(SessionRepository.class);
        this.encoder = mock(PasswordEncoder.class);
        this.service = new SessionService(usuarios, sesiones, encoder, 30);

        var u = new Usuario("user@mail.com", "hash", Set.of(RolUsuario.USUARIO));
        when(usuarios.findByEmail("user@mail.com")).thenReturn(Optional.of(u));
        when(usuarios.findByEmail("userNoExiste@mail.com")).thenReturn(Optional.empty());
        when(encoder.matches("pass", "hash")).thenReturn(true);
    }

    @Test
    void login_ok_creaSessionYGuarda() {
        when(sesiones.save(any(Session.class))).thenAnswer(inv -> inv.getArgument(0));

        var out = service.login("user@mail.com", "pass");

        assertTrue(out.isPresent());
        verify(sesiones, times(1)).save(any(Session.class));
        assertNotNull(out.get().getToken());
        assertTrue(out.get().getExpiresAt().isAfter(Instant.now()));
    }

    @Test
    void login_mailInexistente_vacio() {
        var out = service.login("userNoExiste@mail.com", "pass");
        assertTrue(out.isEmpty());
        verifyNoInteractions(sesiones);
    }

    @Test
    void login_contraseniaIncorrecta_vacio() {
        var out = service.login("user@mail.com", "otraClave"); // intento con clave "pass"

        assertTrue(out.isEmpty());
        // nunca debería intentar guardar una sesión si la clave está mal
        verifyNoInteractions(sesiones);
    }

    @Nested
    class TestsSesiones {

        @BeforeEach
        void setUpSessions() {
            var u2 = new Usuario("a@b.com", "h", Set.of(RolUsuario.USUARIO));
            var s = new Session("tok", u2.getId(), Instant.now().plusSeconds(300));
            var sExp = new Session("tokExp", u2.getId(), Instant.now().minusSeconds(1));

            when(usuarios.findById(u2.getId())).thenReturn(Optional.of(u2));
            when(sesiones.findByToken("tok")).thenReturn(Optional.of(s));
            when(sesiones.findByToken("tokExp")).thenReturn(Optional.of(sExp));
        }

        @Test
        void validate_activoYNoExpirado_devuelveUsuario() {
            var out = service.validate("tok");
            assertTrue(out.isPresent());
            assertEquals("a@b.com", out.get().getEmail());
        }

        @Test
        void validate_expirado_vacio() {
            var out = service.validate("tokExp");
            assertTrue(out.isEmpty());
        }

        @Test
        void logout_sesionValidaLuegoInvalida_repoReal() {
            // Comprobar que es válida
            assertTrue(service.validate("tok").isPresent());

            // Logout
            service.logout("tok");

            // Ahora debe ser inválida
            assertTrue(service.validate("tok").isEmpty());
        }
    }
}
