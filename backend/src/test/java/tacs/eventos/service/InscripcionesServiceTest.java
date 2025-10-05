package tacs.eventos.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.Waitlist;
import tacs.eventos.model.evento.Evento;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.model.inscripcion.InscripcionFactory;
import tacs.eventos.repository.WaitlistRepository;
import tacs.eventos.repository.inscripcion.InscripcionesRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class InscripcionesServiceTest {

    @Mock
    private InscripcionesRepository inscripcionesRepository;

    @Mock
    private WaitlistRepository waitlistRepository;

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

    private Waitlist w1 = new Waitlist(e1);
    private Waitlist w2 = new Waitlist(e2);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        w1.agregar("proximo1");
        w2.agregar("proximo2");

        Mockito.when(inscripcionesRepository.cantidadInscriptos(e1)).thenReturn(9);
        Mockito.when(inscripcionesRepository.cantidadInscriptos(e2)).thenReturn(10);
        Mockito.when(inscripcionesRepository.getInscripcionParaUsuarioYEvento(u1, e1)).thenReturn(Optional.of(i1));
        Mockito.when(inscripcionesRepository.getInscripcionParaUsuarioYEvento(u2, e2)).thenReturn(Optional.of(i2));
        Mockito.when(inscripcionesRepository.getInscripcionParaUsuarioYEvento(u1, e2)).thenReturn(Optional.of(ip1));
        Mockito.when(inscripcionesRepository.getInscripcionParaUsuarioYEvento(u2, e1)).thenReturn(Optional.of(ip2));
        Mockito.when(inscripcionesRepository.getInscripcionPorId("proximo1")).thenReturn(Optional.of(ip1));
        Mockito.when(inscripcionesRepository.getInscripcionPorId("proximo2")).thenReturn(Optional.of(ip2));

        Mockito.when(waitlistRepository.waitlist(e1)).thenReturn(w1);
        Mockito.when(waitlistRepository.waitlist(e2)).thenReturn(w2);
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
        assertDoesNotThrow(() -> Mockito.verify(waitlistRepository, Mockito.atLeastOnce()).waitlist(e2));
        assertDoesNotThrow(
                () -> Mockito.verify(inscripcionesRepository, Mockito.atLeastOnce()).getInscripcionPorId("proximo2"));
    }
}