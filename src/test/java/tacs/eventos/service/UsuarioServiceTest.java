package tacs.eventos.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import tacs.eventos.dto.EventoDTO;
import tacs.eventos.model.Evento;
import tacs.eventos.model.InscripcionEvento;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;
import tacs.eventos.repository.InscripcionesRepository;
import tacs.eventos.repository.UsuarioRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

        when(usuarioRepository.obtenerPorEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn(encodedPassword);

        Usuario usuario = usuarioService.registrar(email, password);

        assertEquals(email, usuario.getEmail());
        assertTrue(usuario.getRoles().contains(RolUsuario.USUARIO));

        verify(usuarioRepository).guardar(usuario);
    }

    @Test
    void registrarUsuarioConEmailExistenteLanzaExcepcion() {
        String email = "test@example.com";
        String password = "password123";

        when(usuarioRepository.obtenerPorEmail(email))
                .thenReturn(Optional.of(new Usuario(email, password, Set.of(RolUsuario.USUARIO))));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.registrar(email, password);
        });

        assertEquals("Email ya registrado", exception.getMessage());
    }

    @Test
    void buscarPorEmailRetornaUsuarioExistente() {
        String email = "test@example.com";
        Usuario usuario = new Usuario(email, "pass", Set.of(RolUsuario.USUARIO));

        when(usuarioRepository.obtenerPorEmail(email)).thenReturn(Optional.of(usuario));

        Optional<Usuario> result = usuarioService.buscarPorEmail(email);

        assertTrue(result.isPresent());
        assertEquals(email, result.get().getEmail());
    }

    @Test
    void obtenerInscripcionesRetornaListaDeEventos() {
        String usuarioId = "123";
        Evento evento1 = new Evento("Evento 1", "Desc 1", null, 60, "Ubicacion", 100, 500, "Categoria");
        Evento evento2 = new Evento("Evento 2", "Desc 2", null, 120, "Ubicacion", 50, 1000, "Categoria");

        List<InscripcionEvento> inscripciones = List.of(new InscripcionEvento(usuarioId, evento1),
                new InscripcionEvento(usuarioId, evento2));

        when(inscripcionesRepository.getInscripcionesPorParticipante(usuarioId)).thenReturn(inscripciones);

        List<EventoDTO> result = usuarioService.obtenerInscripciones(usuarioId);

        assertEquals(2, result.size());
        assertEquals("Evento 1", result.get(0).titulo());
        assertEquals("Evento 2", result.get(1).titulo());
    }
}
