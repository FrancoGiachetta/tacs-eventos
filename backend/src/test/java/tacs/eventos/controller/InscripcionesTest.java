package tacs.eventos.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import tacs.eventos.dto.InscripcionResponse;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.model.inscripcion.InscripcionFactory;
import tacs.eventos.model.waitlist.WaitlistEnMemoriaCompartida;
import tacs.eventos.repository.evento.EventosRepository;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;
import tacs.eventos.repository.usuario.UsuarioRepository;
import tacs.eventos.service.WaitlistService;
import tacs.eventos.service.inscripciones.CupoEventoService;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test funcional del controller + capa de dominio (servicios y modelos), de los endpoints de InscripcionesController.
 * Mockea solamente repositorios.
 */
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class InscripcionesTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InscripcionesRepository inscripcionesRepository;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private EventosRepository eventosRepository;

    @MockBean
    private WaitlistService waitlistService;

    @Autowired
    private ObjectMapper objectMapper;

    private Usuario u1 = new Usuario("pepe@gmail.com", "asd", null);
    private Usuario u2 = new Usuario("pepe@gmail.com", "asd", null);
    private Usuario organizador = new Usuario("org34@gmail.com", "asd", null);

    private Evento e1 = new Evento("GP de Italia", "FORMULA 1 PIRELLI GRAN PREMIO D'ITALIA 2025",
            LocalDateTime.of(2025, 9, 7, 10, 00), 120, "Monza", 2, 200, "F1");

    @Autowired
    private CupoEventoService cupoEventoService;

    @BeforeEach
    void setUp() {
        e1.setOrganizador(organizador);
        when(usuarioRepository.findById(u1.getId())).thenReturn(Optional.of(u1));
        when(eventosRepository.findById(e1.getId())).thenReturn(Optional.of(e1));
        Authentication auth = new UsernamePasswordAuthenticationToken(u1, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Nested
    class GetInscripcion {
        @Test
        void unUsuarioPuedeVerSuInscripcionConfirmada() throws Exception {
            // Deja al usuario inscripto
            InscripcionEvento i1 = InscripcionFactory.confirmada(u1, e1);
            when(inscripcionesRepository.noCanceladaParaParticipanteYEvento(u1, e1)).thenReturn(Optional.of(i1));
            // Mockea el pedido GET y verifica que retorne 200 OK y la inscripción
            String url = "/api/v1/evento/" + e1.getId() + "/inscripcion/" + u1.getId();
            mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(
                    content().string(objectMapper.writeValueAsString(InscripcionResponse.confirmada(e1.getId()))));
        }

        @Test
        void unUsuarioPuedeVerSuInscripcionEnWaitlist() throws Exception {
            mockearInscripcionEnWatilist(u1, e1);

            // Mockea el pedido GET y verifica que retorne 200 OK y la inscripción
            String url = "/api/v1/evento/" + e1.getId() + "/inscripcion/" + u1.getId();
            mockMvc.perform(get(url)).andExpect(status().isOk()).andExpect(
                    content().string(objectMapper.writeValueAsString(InscripcionResponse.enWaitlist(e1.getId()))));
        }

        @Test
        void unUsuarioNoPuedeVerLaInscripcionDeOtroUsuario() throws Exception {
            mockMvc.perform(get("/api/v1/evento/" + e1.getId() + "/inscripcion/" + u2.getId()))
                    .andExpect(status().isNotFound());
        }

        @Test
        void siElEventoNoExisteRetorna404() throws Exception {
            mockMvc.perform(get("/api/v1/evento/" + "esteEventoNoExiste" + "/inscripcion/" + u1.getId()))
                    .andExpect(status().isNotFound()).andExpect(status().reason("Evento no encontrado"));
        }
    }

    private WaitlistEnMemoriaCompartida mockearInscripcionEnWatilist(Usuario u, Evento e) {
        // Crea una waitlist de prueba, en la que está el usuario
        InscripcionEvento i1 = InscripcionFactory.pendiente(u, e);
        WaitlistEnMemoriaCompartida w1 = new WaitlistEnMemoriaCompartida(e, new LinkedList<>(), inscripcionesRepository);
        w1.agregar(i1.getId());
        when(waitlistService.waitlist(e)).thenReturn(w1);
        when(inscripcionesRepository.noCanceladaParaParticipanteYEvento(u, e)).thenReturn(Optional.of(i1));
        when(inscripcionesRepository.findById(i1.getId())).thenReturn(Optional.of(i1));
        return w1;
    }

    @Nested
    class CrearInscripcion {
        @Test
        void unUsuarioSePuedeInscribirDirectamenteAUnEventoConCupo() throws Exception {
            // Waitlist vacía
            WaitlistEnMemoriaCompartida w1 = new WaitlistEnMemoriaCompartida(e1, new LinkedList<>(), inscripcionesRepository);
            when(waitlistService.waitlist(e1)).thenReturn(w1);

            // Mockea el pedido POST y verifica que retorne 201 CREATED y la inscripción
            String url = "/api/v1/evento/" + e1.getId() + "/inscripcion/" + u1.getId();
            mockMvc.perform(post(url)).andExpect(status().isCreated()).andExpect(header().string("Location", url));

            // Verifica que se haya guardado la inscripción en el repo
            verify(inscripcionesRepository).save(InscripcionFactory.confirmada(u1, e1));
        }

        @Test
        void unUsuarioPuedeIngresarALaWaitlistDeUnEventoSinCupo() throws Exception {
            // Crea una waitlist de prueba, vacía
            WaitlistEnMemoriaCompartida w1 = mock(WaitlistEnMemoriaCompartida.class);
            when(waitlistService.waitlist(e1)).thenReturn(w1);
            // Hace que el evento no tenga cupo
            cupoEventoService.obtenerCupo(e1);
            cupoEventoService.obtenerCupo(e1);
            // Mockea el pedido POST y verifica que retorne 201 CREATED y apunte a la inscripción
            String url = "/api/v1/evento/" + e1.getId() + "/inscripcion/" + u1.getId();
            mockMvc.perform(post(url)).andExpect(status().isCreated()).andExpect(header().string("Location", url));

            verify(inscripcionesRepository).save(argThat((InscripcionEvento i) -> i.getEvento().equals(e1)
                    && i.getParticipante().equals(u1) && i.estaPendiente()));
            verify(w1, times(1)).agregar(any());
        }

        @Test
        void siElUsuarioYaEstaEnWaitlistNoSeGeneraUnaNuevaInscripcionYRetorna200okYLaInscripcion() throws Exception {
            // Crea una waitlist de prueba, en la que está ese usuario
            mockearInscripcionEnWatilist(u1, e1);

            // Mockea el pedido POST y verifica que retorne SEE OTHER y la inscripción
            String url = "/api/v1/evento/" + e1.getId() + "/inscripcion/" + u1.getId();
            mockMvc.perform(post(url)).andExpect(status().isSeeOther()).andExpect(header().string("Location", url));

            // Verifica que no se haya creado ninguna inscripción
            verify(inscripcionesRepository, never()).save(any());
        }

        @Test
        void siElUsuarioNoExisteMuestraElErrorYNoRealizaLaInscripcion() throws Exception {
            mockMvc.perform(post("/api/v1/evento/" + e1.getId() + "/inscripcion/" + "esteUsuarioNoExiste"))
                    .andExpect(status().isForbidden())
                    .andExpect(status().reason("Solamente pueden crear una inscripción "
                            + "el usuario que se va a inscribir, o el organizador del evento"));

            verify(inscripcionesRepository, never()).save(any());
        }

        @Test
        void siElEventoNoExisteMuestraElErrorYNoRealizaLaInscripcion() throws Exception {
            mockMvc.perform(post("/api/v1/evento/" + "esteEventoNoExiste" + "/inscripcion/" + u1.getId()))
                    .andExpect(status().isNotFound()).andExpect(status().reason("Evento no encontrado"));

            verifyNoInteractions(inscripcionesRepository);
        }

    }

    @Nested
    class CancelacionDeInscripcion {
        @Test
        void unUsuarioPuedeCancelarSuInscripcionConfirmada() throws Exception {
            // Waitlist vacía
            WaitlistEnMemoriaCompartida w1 = new WaitlistEnMemoriaCompartida(e1, new LinkedList<>(), inscripcionesRepository);
            when(waitlistService.waitlist(e1)).thenReturn(w1);

            // Deja al usuario inscripto
            InscripcionEvento i1 = InscripcionFactory.confirmada(u1, e1);
            when(inscripcionesRepository.noCanceladaParaParticipanteYEvento(u1, e1)).thenReturn(Optional.of(i1));

            mockMvc.perform(delete("/api/v1/evento/" + e1.getId() + "/inscripcion/" + u1.getId()));

            assertEquals(EstadoInscripcion.CANCELADA, i1.getEstado());
        }

        @Test
        void unOrganizadorPuedeCancelarLaInscripcionDeUnUsuario() throws Exception {
            // Cambia el usuario autenticado por el organizador
            Authentication auth = new UsernamePasswordAuthenticationToken(organizador, null);
            SecurityContextHolder.getContext().setAuthentication(auth);

            // Waitlist vacía
            WaitlistEnMemoriaCompartida w1 = new WaitlistEnMemoriaCompartida(e1, new LinkedList<>(), inscripcionesRepository);
            when(waitlistService.waitlist(e1)).thenReturn(w1);

            // Deja al usuario inscripto
            InscripcionEvento i1 = InscripcionFactory.confirmada(u1, e1);
            when(inscripcionesRepository.noCanceladaParaParticipanteYEvento(u1, e1)).thenReturn(Optional.of(i1));

            mockMvc.perform(delete("/api/v1/evento/" + e1.getId() + "/inscripcion/" + u1.getId()));

            assertEquals(EstadoInscripcion.CANCELADA, i1.getEstado());
        }

        @Test
        void unUsuarioNoPuedeCancelarLaInscripcionDeOtroUsuario() throws Exception {
            // Deja al usuario 2 inscripto
            InscripcionEvento i1 = InscripcionFactory.confirmada(u2, e1);
            when(inscripcionesRepository.noCanceladaParaParticipanteYEvento(u2, e1)).thenReturn(Optional.of(i1));

            mockMvc.perform(delete("/api/v1/evento/" + e1.getId() + "/inscripcion/" + u2.getId()))
                    .andExpect(status().isNoContent());

            assertEquals(EstadoInscripcion.CONFIRMADA, i1.getEstado());
            verifyNoInteractions(inscripcionesRepository, waitlistService);
        }

        @Test
        void unUsuarioPuedeCancelarSuInscripcionEnWaitlist() throws Exception {
            // Crea una waitlist de prueba, en la que está ese usuario
            mockearInscripcionEnWatilist(u1, e1);

            mockMvc.perform(delete("/api/v1/evento/" + e1.getId() + "/inscripcion/" + u1.getId()))
                    .andExpect(status().isNoContent());

            assertEquals(EstadoInscripcion.CANCELADA,
                    inscripcionesRepository.noCanceladaParaParticipanteYEvento(u1, e1).get().getEstado());
        }

        @Test
        void siElUsuarioNoExisteNoHaceNada() throws Exception {
            mockMvc.perform(delete("/api/v1/evento/" + e1.getId() + "/inscripcion/" + "esteUsuarioNoExiste"))
                    .andExpect(status().isNoContent());

            verifyNoInteractions(inscripcionesRepository, waitlistService);
        }

        @Test
        void siElEventoNoExisteRetorna404NotFound() throws Exception {
            mockMvc.perform(delete("/api/v1/evento/" + "esteEventoNoExiste" + "/inscripcion/" + u1.getId()))
                    .andExpect(status().isNotFound()).andExpect(status().reason("Evento no encontrado"));

            verifyNoInteractions(inscripcionesRepository, waitlistService);
        }
    }

    @Nested
    class PromocionDesdeWaitlistAlAbrirseLugar {

        private InscripcionEvento i1;

        @BeforeEach
        void setUp() {
            // Deja al usuario 1 inscripto
            i1 = InscripcionFactory.confirmada(u1, e1);
            when(inscripcionesRepository.noCanceladaParaParticipanteYEvento(u1, e1)).thenReturn(Optional.of(i1));
        }

        @Test
        void siLaInscripcionEnWaitlistFueCanceladaNoSePromueve() throws Exception {
            // Crea una inscripción pendiente (en waitlist) para u2
            InscripcionEvento inscripcionWaitlist = InscripcionFactory.pendiente(u2, e1);
            inscripcionWaitlist.cancelar();
            WaitlistEnMemoriaCompartida w1 = new WaitlistEnMemoriaCompartida(e1, new LinkedList<>(), inscripcionesRepository);
            w1.agregar(inscripcionWaitlist.getId());
            when(waitlistService.waitlist(e1)).thenReturn(w1);
            when(inscripcionesRepository.noCanceladaParaParticipanteYEvento(u2, e1)).thenReturn(Optional.of(inscripcionWaitlist));
            when(inscripcionesRepository.findById(inscripcionWaitlist.getId())).thenReturn(Optional.of(inscripcionWaitlist));

            // Ahora cancela la inscripción confirmada de u1 (debería intentar promover a u2, pero está cancelada)
            mockMvc.perform(delete("/api/v1/evento/" + e1.getId() + "/inscripcion/" + u1.getId()))
                    .andExpect(status().isNoContent());

            // Verifica que la inscripción de u2 sigue cancelada y no fue promovida
            assertEquals(EstadoInscripcion.CANCELADA, inscripcionWaitlist.getEstado());
            verify(inscripcionesRepository, never()).save(argThat((InscripcionEvento i) -> i.getParticipante().equals(u2) && i.estaConfirmada()));
        }

        @Test
        void alCancelarUnaInscripcionConfirmadaSePromueveAlPrimeroDeLaWaitlist() throws Exception {
            // Crea una waitlist de prueba, en la que está el usuario 2
            WaitlistEnMemoriaCompartida w1 = mockearInscripcionEnWatilist(u2, e1);

            // Cancela la inscripción del usuario 1
            mockMvc.perform(delete("/api/v1/evento/" + e1.getId() + "/inscripcion/" + u1.getId()));

            // Chequea que la waitlist haya quedado vacía
            assertEquals(Optional.empty(), w1.proxima());
            // Chequea que el usuario 2 haya quedado inscripto (chequea con una inscripción directa. En realidad sería
            // una inscripción desde waitlist, no directa, pero como el id de inscripción es (usuario, evento), sirve
            // igual.
            verify(inscripcionesRepository).save(InscripcionFactory.confirmada(u2, e1));
        }

        @Test
        void sePuedeCancelarUnaInscripcionAunqueNoHayaNadieEnLaWaitlistParaSerPromovido() throws Exception {
            // La waitlist está vacía
            when(waitlistService.waitlist(e1)).thenReturn(new WaitlistEnMemoriaCompartida(e1, new LinkedList<>(), inscripcionesRepository));

            mockMvc.perform(delete("/api/v1/evento/" + e1.getId() + "/inscripcion/" + u1.getId()));

            assertEquals(EstadoInscripcion.CANCELADA, i1.getEstado());
        }
    }

}