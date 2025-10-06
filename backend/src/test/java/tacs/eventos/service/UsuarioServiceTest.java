package tacs.eventos.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import tacs.eventos.config.TestMongoConfiguration;
import tacs.eventos.config.TestRedisConfiguration;
import tacs.eventos.dto.EstadoInscripcionResponse;
import tacs.eventos.dto.InscripcionResponse;
import tacs.eventos.model.evento.Evento;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.model.inscripcion.InscripcionFactory;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;
import tacs.eventos.repository.usuario.UsuarioRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import({ TestRedisConfiguration.class, TestMongoConfiguration.class })
@ActiveProfiles("test")
@Testcontainers
class UsuarioServiceTest {

    private UsuarioRepository usuarioRepository;
    private InscripcionesRepository inscripcionesRepository;
    private PasswordEncoder passwordEncoder;
    private UsuarioService usuarioService;

    @BeforeEach
    void setUp() {
        usuarioRepository = mock(UsuarioRepository.class);
        inscripcionesRepository = mock(InscripcionesRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);

        usuarioService = new UsuarioService(usuarioRepository, inscripcionesRepository, passwordEncoder);
    }

    @Test
    void registrarUsuarioExitosamente() {
        String email = "test@example.com";
        String password = "password123";
        String encodedPassword = "encodedPassword";

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        Usuario usuario = usuarioService.registrar(email, password);

        assertEquals(email, usuario.getEmail());
        assertTrue(usuario.getRoles().contains(RolUsuario.USUARIO));

        verify(usuarioRepository).save(usuario);
    }

    @Test
    void registrarUsuarioConEmailExistenteLanzaExcepcion() {
        String email = "test@example.com";
        String password = "password123";

        when(usuarioRepository.findByEmail(email))
                .thenReturn(Optional.of(new Usuario(email, password, Set.of(RolUsuario.USUARIO))));

        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> usuarioService.registrar(email, password));

        assertEquals("Email ya registrado", exception.getMessage());
    }

    @Test
    void buscarPorEmailRetornaUsuarioExistente() {
        String email = "test@example.com";
        Usuario usuario = new Usuario(email, "pass", Set.of(RolUsuario.USUARIO));

        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.buscarPorEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }

    @Test
    void obtenerInscripcionesNoCanceladasRetornaListaDeEventosConInscripcionNoCancelada() {
        Usuario usuario = new Usuario("asd@mail.com", "asd", Set.of(RolUsuario.USUARIO));

        usuarioRepository.save(usuario);
        when(usuarioRepository.findByEmail(usuario.getId())).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findById(usuario.getId())).thenReturn(Optional.of(usuario));
        Evento evento1 = new Evento("Evento 1", "Desc 1", null, 60, "Ubicacion", 100, 500, "Categoria");
        Evento evento2 = new Evento("Evento 2", "Desc 2", null, 120, "Ubicacion", 50, 1000, "Categoria");
        Evento evento3 = new Evento("Evento 3", "Desc 3", null, 120, "Ubicacion", 50, 1000, "Categoria");

        List<InscripcionEvento> inscripciones = List.of(InscripcionFactory.confirmada(usuario, evento1),
                InscripcionFactory.confirmada(usuario, evento2), InscripcionFactory.pendiente(usuario, evento3));

        when(inscripcionesRepository.noCanceladasDeParticipante(usuario)).thenReturn(inscripciones);

        var result = usuarioService.obtenerInscripcionesNoCanceladas(usuario.getId());

        var esperado = Set.of(new InscripcionResponse(evento1.getId(), EstadoInscripcionResponse.CONFIRMADA),
                new InscripcionResponse(evento2.getId(), EstadoInscripcionResponse.CONFIRMADA),
                new InscripcionResponse(evento3.getId(), EstadoInscripcionResponse.PENDIENTE));
        assertEquals(esperado, new HashSet<>(result));
    }
}
