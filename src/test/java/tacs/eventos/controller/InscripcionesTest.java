package tacs.eventos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tacs.eventos.dto.EstadoInscripcionResponse;
import tacs.eventos.dto.InscripcionRequest;
import tacs.eventos.dto.InscripcionResponse;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.Waitlist;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.model.inscripcion.InscripcionFactory;
import tacs.eventos.repository.WaitlistRepository;
import tacs.eventos.repository.evento.EventosRepository;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;
import tacs.eventos.repository.usuario.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test funcional del controller + capa de dominio (servicios y modelos), de los endpoints de InscripcionesController.
 * Mockea solamente repositorios.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class InscripcionesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InscripcionesRepository inscripcionesRepository;

    @MockitoBean
    private UsuarioRepository usuarioRepository;

    @MockitoBean
    private EventosRepository eventosRepository;

    @MockitoBean
    private WaitlistRepository waitlistRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario u1 = new Usuario("pepe@gmail.com", "asd", null);
    private Usuario u2 = new Usuario("pepe@gmail.com", "asd", null);

    private Evento e1 = new Evento("GP de Italia", "FORMULA 1 PIRELLI GRAN PREMIO D'ITALIA 2025",
            LocalDateTime.of(2025, 9, 7, 10, 00), 120, "Monza", 2, 200, "F1");

    @BeforeEach
    void setUp() {
        when(usuarioRepository.obtenerPorId(u1.getId())).thenReturn(Optional.of(u1));
        when(eventosRepository.getEvento(e1.getId())).thenReturn(Optional.of(e1));
    }

    @Nested
    class Inscripcion {
        @Test
        void unUsuarioSePuedeInscribirDirectamenteAUnEventoConCupo() throws Exception {
            // Waitlist vacía
            Waitlist w1 = new Waitlist(e1);
            when(waitlistRepository.waitlist(e1)).thenReturn(w1);

            // Mockea el pedido POST y verifica que retorne 201 CREATED y la inscripción
            mockMvc.perform(post("/api/v1/inscripciones").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new InscripcionRequest(u1.getId(), e1.getId()))))
                    .andExpect(status().isCreated()).andExpect(content().json(objectMapper.writeValueAsString(
                            new InscripcionResponse(e1.getId(), EstadoInscripcionResponse.CONFIRMADA))));

            // Verifica que se haya guardado la inscripción en el repo
            verify(inscripcionesRepository).guardarInscripcion(InscripcionFactory.directa(u1, e1));
        }

        @Test
        void unUsuarioPuedeIngresarALaWaitlistDeUnEventoSinCupo() throws Exception {
            // Crea una waitlist de prueba
            Waitlist w1 = new Waitlist(e1);
            when(waitlistRepository.waitlist(e1)).thenReturn(w1);
            // Hace que el evento no tenga cupo
            when(inscripcionesRepository.cantidadInscriptos(e1)).thenReturn(2);
            // Mockea el pedido POST y verifica que retorne 201 CREATED y la inscripción
            mockMvc.perform(post("/api/v1/inscripciones").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new InscripcionRequest(u1.getId(), e1.getId()))))
                    .andExpect(status().isCreated()).andExpect(content().json(objectMapper.writeValueAsString(
                            new InscripcionResponse(e1.getId(), EstadoInscripcionResponse.PENDIENTE))));

            assertEquals(w1.candidatos(), List.of(u1));
        }

        @Test
        void siElUsuarioYaEstaEnWaitlistNoSeGeneraUnaNuevaInscripcionYRetorna200okYLaInscripcion() throws Exception {
            // Crea una waitlist de prueba, en la que está ese usuario
            Waitlist w1 = new Waitlist(e1);
            w1.agregar(u1);
            when(waitlistRepository.waitlist(e1)).thenReturn(w1);

            // Mockea el pedido POST y verifica que retorne 200 OK y la inscripción
            mockMvc.perform(post("/api/v1/inscripciones").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new InscripcionRequest(u1.getId(), e1.getId()))))
                    .andExpect(status().isOk()).andExpect(content().json(objectMapper.writeValueAsString(
                            new InscripcionResponse(e1.getId(), EstadoInscripcionResponse.PENDIENTE))));

            // Verifica que la waitlist no haya sido modificada
            assertEquals(w1.candidatos(), List.of(u1));
            // Verifica que no se haya creado ninguna inscripción
            verify(inscripcionesRepository, never()).guardarInscripcion(any());
        }

        @Test
        void siElUsuarioNoExisteMuestraElErrorYNoRealizaLaInscripcion() throws Exception {
            mockMvc.perform(post("/api/v1/inscripciones").contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(new InscripcionRequest("esteUsuarioNoExiste", e1.getId()))))
                    .andExpect(status().isBadRequest()).andExpect(status().reason("Usuario no encontrado"));

            verify(inscripcionesRepository, never()).guardarInscripcion(any());
        }

        @Test
        void siElEventoNoExisteMuestraElErrorYNoRealizaLaInscripcion() throws Exception {
            mockMvc.perform(post("/api/v1/inscripciones").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new InscripcionRequest(u1.getId(), "esteEventoNoExiste"))))
                    .andExpect(status().isBadRequest()).andExpect(status().reason("Evento no encontrado"));

            verifyNoInteractions(inscripcionesRepository);
        }

    }

    @Nested
    class CancelacionDeInscripcion {
        @Test
        void unUsuarioPuedeCancelarUnaInscripcionConfirmada() throws Exception {
            // Waitlist vacía
            Waitlist w1 = new Waitlist(e1);
            when(waitlistRepository.waitlist(e1)).thenReturn(w1);

            // Deja al usuario inscripto
            InscripcionEvento i1 = InscripcionFactory.directa(u1, e1);
            when(inscripcionesRepository.getInscripcion(u1, e1)).thenReturn(Optional.of(i1));

            mockMvc.perform(delete("/api/v1/inscripciones").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new InscripcionRequest(u1.getId(), e1.getId()))));

            assertEquals(i1.getEstado(), EstadoInscripcion.CANCELADA);
        }

        @Test
        void unUsuarioPuedeCancelarUnaInscripcionEnWaitlist() throws Exception {
            // Crea una waitlist de prueba, en la que está ese usuario
            Waitlist w1 = new Waitlist(e1);
            w1.agregar(u1);
            when(waitlistRepository.waitlist(e1)).thenReturn(w1);

            mockMvc.perform(delete("/api/v1/inscripciones").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new InscripcionRequest(u1.getId(), e1.getId()))));

            assertEquals(w1.candidatos(), List.of());
        }

        @Test
        void siElUsuarioNoExisteRetorna400BadRequest() throws Exception {
            mockMvc.perform(delete("/api/v1/inscripciones").contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(new InscripcionRequest("Este usuario no existe", e1.getId()))))
                    .andExpect(status().isBadRequest()).andExpect(status().reason("Usuario no encontrado"));

            verifyNoInteractions(inscripcionesRepository);
            verifyNoInteractions(waitlistRepository);
        }

        @Test
        void siElEventoNoExisteRetorna400BadRequest() throws Exception {
            mockMvc.perform(delete("/api/v1/inscripciones").contentType(MediaType.APPLICATION_JSON).content(
                    objectMapper.writeValueAsString(new InscripcionRequest(u1.getId(), "Este Evento no existe"))))
                    .andExpect(status().isBadRequest()).andExpect(status().reason("Evento no encontrado"));

            verifyNoInteractions(inscripcionesRepository);
            verifyNoInteractions(waitlistRepository);
        }
    }

    @Nested
    class PromocionDesdeWaitlistAlAbrirseLugar {

        @Test
        void alCancelarUnaInscripcionConfirmadaSePromueveAlPrimeroDeLaWaitlist() throws Exception {
            // Deja al usuario 1 inscripto
            InscripcionEvento i1 = InscripcionFactory.directa(u1, e1);
            when(inscripcionesRepository.getInscripcion(u1, e1)).thenReturn(Optional.of(i1));

            // Crea una waitlist de prueba, en la que está el usuario 2
            Waitlist w1 = new Waitlist(e1);
            w1.agregar(u2);
            when(waitlistRepository.waitlist(e1)).thenReturn(w1);

            // Cancela la inscripción del usuario 1
            mockMvc.perform(delete("/api/v1/inscripciones").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new InscripcionRequest(u1.getId(), e1.getId()))));

            // Chequea que la waitlist haya quedado vacía
            assertEquals(List.of(), w1.candidatos());
            // Chequea que el usuario 2 haya quedado inscripto (chequea con una inscripción directa. En realidad sería
            // una inscripción desde waitlist, no directa, pero como el id de inscripción es (usuario, evento), sirve
            // igual.
            verify(inscripcionesRepository).guardarInscripcion(InscripcionFactory.directa(u2, e1));
        }

        @Test
        void sePuedeCancelarUnaInscripcionAunqueNoHayaNadieEnLaWaitlistParaSerPromovido() throws Exception {
            // La waitlist está vacía
            when(waitlistRepository.waitlist(e1)).thenReturn(new Waitlist(e1));

            // Deja al usuario inscripto
            InscripcionEvento i1 = InscripcionFactory.directa(u1, e1);
            when(inscripcionesRepository.getInscripcion(u1, e1)).thenReturn(Optional.of(i1));

            mockMvc.perform(delete("/api/v1/inscripciones").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(new InscripcionRequest(u1.getId(), e1.getId()))));
        }
    }

}
