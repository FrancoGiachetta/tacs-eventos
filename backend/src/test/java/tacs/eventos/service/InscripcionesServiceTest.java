package tacs.eventos.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.evento.Evento;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.model.inscripcion.InscripcionFactory;
import tacs.eventos.model.waitlist.Waitlist;
import tacs.eventos.model.waitlist.WaitlistEnMemoriaCompartida;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;
import tacs.eventos.service.inscripciones.InscripcionesService;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InscripcionesServiceTest {

    @Mock
    private InscripcionesRepository inscripcionesRepository;

    @Mock
    private WaitlistService waitlistService;

    @InjectMocks
    private InscripcionesService inscripcionesService;

    private Usuario u1 = new Usuario("test@mail.com", "hash", Set.of(RolUsuario.USUARIO));
    private Usuario u2 = new Usuario("test2@mail.com", "hash", Set.of(RolUsuario.USUARIO));

    private Evento e1 = new Evento("Concierto", "", LocalDateTime.now(), 100, "", 10, 100, "Musica");
    private Evento e2 = new Evento("Partido", "", LocalDateTime.now(), 100, "", 10, 100, "Deportes");

    private InscripcionEvento i1 = InscripcionFactory.confirmada(u1, e1);
    private InscripcionEvento i2 = InscripcionFactory.confirmada(u2, e2);
    private InscripcionEvento ip1 = InscripcionFactory.pendiente(u1, e2);
    private InscripcionEvento ip2 = InscripcionFactory.pendiente(u2, e1);

    private Waitlist w1 = new WaitlistEnMemoriaCompartida(e1, new LinkedList<>(), inscripcionesRepository);
    private Waitlist w2 = new WaitlistEnMemoriaCompartida(e2, new LinkedList<>(), inscripcionesRepository);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        w1.agregar("proximo1");
        w2.agregar("proximo2");

        Mockito.when(inscripcionesRepository.countByEvento(e1)).thenReturn(9);
        Mockito.when(inscripcionesRepository.countByEvento(e2)).thenReturn(10);
        Mockito.when(inscripcionesRepository.noCanceladaParaParticipanteYEvento(u1, e1)).thenReturn(Optional.of(i1));
        Mockito.when(inscripcionesRepository.noCanceladaParaParticipanteYEvento(u2, e2)).thenReturn(Optional.of(i2));
        Mockito.when(inscripcionesRepository.noCanceladaParaParticipanteYEvento(u1, e2)).thenReturn(Optional.of(ip1));
        Mockito.when(inscripcionesRepository.noCanceladaParaParticipanteYEvento(u2, e1)).thenReturn(Optional.of(ip2));
        Mockito.when(inscripcionesRepository.findById("proximo1")).thenReturn(Optional.of(ip1));
        Mockito.when(inscripcionesRepository.findById("proximo2")).thenReturn(Optional.of(ip2));

        Mockito.when(waitlistService.waitlist(e1)).thenReturn(w1);
        Mockito.when(waitlistService.waitlist(e2)).thenReturn(w2);
    }

    @Test
    void inscribirOMandarAWaitlistConEventoNoLleno() {
        var i = inscripcionesService.inscribirOMandarAWaitlist(e1, u1);
        assertTrue(i.isPresent());
    }

    @Test
    void inscribirOMandarAWaitlistConEventoLleno() {
        var i = inscripcionesService.inscribirOMandarAWaitlist(e2, u2);
        assertTrue(i.isEmpty());
    }

    @Test
    void cancelarInscripcion() {
        inscripcionesService.cancelarInscripcion(e2, u2);
        assertEquals(EstadoInscripcion.CANCELADA, i2.getEstado());
        assertDoesNotThrow(() -> Mockito.verify(waitlistService, Mockito.atLeastOnce()).waitlist(e2));
        assertDoesNotThrow(
                () -> Mockito.verify(inscripcionesRepository, Mockito.atLeastOnce()).findById("proximo2"));
    }
}