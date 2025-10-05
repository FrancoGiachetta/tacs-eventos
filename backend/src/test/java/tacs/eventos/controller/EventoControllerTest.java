package tacs.eventos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import tacs.eventos.dto.CreacionEventoRequest;
import tacs.eventos.dto.EventoResponse;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Session;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.evento.Evento;
import tacs.eventos.service.EventoService;
import tacs.eventos.service.InscripcionesService;
import tacs.eventos.service.SessionService;
import tacs.eventos.service.UsuarioService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

@WebMvcTest(EventoController.class)
@AutoConfigureMockMvc(addFilters = false)
class EventoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper; // para serializar JSON

    @MockBean
    private EventoService eventoService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private InscripcionesService inscripcionesService;

    @MockBean
    private SessionService sessionService;

    @MockBean
    private ModelMapper modelMapper;

    Evento e1;
    EventoResponse r1;
    Evento e2;
    EventoResponse r2;

    Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("user@mail.com", "hash", Set.of(RolUsuario.USUARIO));

        e1 = new Evento();
        e1.setId("e1");
        e1.setTitulo("Concierto");
        e1.setOrganizador(usuario);
        r1 = new EventoResponse();
        r1.setId("e1");
        r1.setTitulo("Concierto");

        e2 = new Evento();
        e2.setId("e2");
        r2 = new EventoResponse();
        r2.setId("e2");

        Mockito.when(eventoService.buscarEventoPorId("e1")).thenReturn(Optional.of(e1));

        Mockito.when(eventoService.listarEventos()).thenReturn(List.of(e1, e2));
        Mockito.when(modelMapper.map(e1, EventoResponse.class)).thenReturn(r1);
        Mockito.when(modelMapper.map(e2, EventoResponse.class)).thenReturn(r2);

        Authentication auth = new UsernamePasswordAuthenticationToken(usuario, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void crearEvento_devuelve201Created() throws Exception {
        CreacionEventoRequest request = new CreacionEventoRequest();
        request.setCategoria("Música");
        request.setCupoMaximo(100);
        request.setTitulo("Concierto");
        request.setFechaHoraInicio(LocalDateTime.now());
        request.setDuracionMinutos(120);
        request.setDescripcion("descripcion");
        request.setUbicacion("ubic");

        Mockito.when(modelMapper.map(any(CreacionEventoRequest.class), any())).thenReturn(e1);

        assertDoesNotThrow(() -> mockMvc
                .perform(post("/api/v1/evento").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)).principal(() -> usuario.getId())
                        .header("Authorization", "Bearer token")) // simula usuario autenticado
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/evento"))));
    }

    @Test
    void obtenerEvento_existente_devuelve200() throws Exception {
        assertDoesNotThrow(() -> mockMvc.perform(get("/api/v1/evento/e1")).andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is("e1"))).andExpect(jsonPath("$.titulo", is("Concierto"))));
    }

    @Test
    void obtenerEvento_inexistente_devuelve404() throws Exception {
        Mockito.when(eventoService.buscarEventoPorId("noExiste")).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> mockMvc.perform(get("/api/v1/evento/noExiste")).andExpect(status().isNotFound()));
    }

    @Test
    void listarEventos_sinFiltros_devuelveLista() throws Exception {
        assertDoesNotThrow(() -> mockMvc.perform(get("/api/v1/evento")).andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))).andExpect(jsonPath("$[0].id", is("e1")))
                .andExpect(jsonPath("$[1].id", is("e2"))));
    }
}
