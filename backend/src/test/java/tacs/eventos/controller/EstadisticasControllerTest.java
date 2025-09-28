package tacs.eventos.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;
import tacs.eventos.service.EstadisticaService;
import tacs.eventos.service.EventoService;
import tacs.eventos.service.SessionService;
import tacs.eventos.service.UsuarioService;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EstadisticasControllerTest {

    @Mock
    private EstadisticaService estadisticaService;

    @Mock
    private SessionService sessionService;

    @Mock
    private EventoService eventoService;

    @Mock
    private UsuarioService usuarioService;

    @InjectMocks
    private EstadisticasController controller;

    private final String VALID_AUTH_HEADER = "Bearer valid-admin-token";
    private Usuario adminUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup admin user mock
        adminUser = new Usuario("admin@test.com", "hashedPassword", Set.of(RolUsuario.ADMIN));

        // Mock session service to return admin user for valid token
        when(sessionService.validate("valid-admin-token")).thenReturn(Optional.of(adminUser));
    }

    @Test
    void testCantidadEventos() throws Exception {
        when(estadisticaService.cantidadEventos()).thenReturn(10);

        ResponseEntity<Integer> response = controller.cantidadEventos(VALID_AUTH_HEADER);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(10, response.getBody());
        verify(estadisticaService).cantidadEventos();
    }

    @Test
    void testCantidadInscripciones() {
        when(estadisticaService.cantidadInscripciones()).thenReturn(20);

        ResponseEntity<Long> response = controller.cantidadInscripciones(VALID_AUTH_HEADER);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(20, response.getBody());
        verify(estadisticaService).cantidadInscripciones();
    }

    @Test
    void testTasaConversionWL() {
        String eventoId = "123";
        when(estadisticaService.calcularTasaConversionWL(eventoId)).thenReturn(50);

        ResponseEntity<Integer> response = controller.tasaConversionWL(eventoId, VALID_AUTH_HEADER);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(50, response.getBody());
        verify(estadisticaService).calcularTasaConversionWL(eventoId);
    }

    @Test
    void testCantidadEventos_Unauthorized() throws Exception {
        // Mock invalid token
        when(sessionService.validate("invalid-token")).thenReturn(Optional.empty());

        ResponseEntity<Integer> response = controller.cantidadEventos("Bearer invalid-token");

        assertEquals(403, response.getStatusCode().value());
        verify(estadisticaService, never()).cantidadEventos();
    }

    @Test
    void testCantidadInscripciones_Unauthorized() {
        // Mock non-admin user
        Usuario regularUser = new Usuario("user@test.com", "hashedPassword", Set.of(RolUsuario.USUARIO));
        when(sessionService.validate("regular-token")).thenReturn(Optional.of(regularUser));

        ResponseEntity<Integer> response = controller.cantidadInscripciones("Bearer regular-token");

        assertEquals(403, response.getStatusCode().value());
        verify(estadisticaService, never()).cantidadInscripciones();
    }
}
