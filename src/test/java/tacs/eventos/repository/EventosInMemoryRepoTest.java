package tacs.eventos.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import tacs.eventos.model.Evento;

@SpringBootTest
class EventosInMemoryRepoTest {
    private EventosInMemoryRepo repo;
    private Evento e1;
    private Evento e2;
    private Evento e3;

    @BeforeEach
    void setUp() {
        this.repo = new EventosInMemoryRepo();
        this.e1 = new Evento(
            "Evento 1", "", null, 1, "", 0, 0, "Deporte"
        );
        this.e2 = new Evento(
            "Evento 2", "", null, 1, "", 0, 0, "Moda"
        );
        this.e3 = new Evento(
            "Evento 3", "", null, 1, "", 0, 0, "Deporte"
        );
        this.repo.guardarEvento(e1);
        this.repo.guardarEvento(e2);
        this.repo.guardarEvento(e3);
    }

    @Test
    void todos() {
        var eventos = this.repo.todos();
        assertEquals(3, eventos.size());
        assertTrue(eventos.contains(this.e1));
        assertTrue(eventos.contains(this.e2));
        assertTrue(eventos.contains(this.e3));
    }

    @Test
    void getEvento() {
        var evento = this.repo.getEvento(this.e2.getId());
        assertTrue(evento.isPresent());
        assertEquals(this.e2, evento.get());
    }

    @Test
    void getEventosPorOrganizador() {
        return;
    }

    @Test
    void getEventosPorCategoria() {
        var eventosDeporte = this.repo.getEventosPorCategoria("Deporte");
        var eventosModa = this.repo.getEventosPorCategoria("Moda");

        assertTrue(eventosDeporte.contains(this.e1));
        assertTrue(eventosDeporte.contains(this.e3));

        assertTrue(eventosModa.contains(this.e2));
    }

    @Test
    void guardarEvento() {
        var e4 = new Evento(
            "Evento 4", "", null, 1, "", 0, 0, "Deporte"
        );
        var e4Id = e4.getId();

        this.repo.guardarEvento(e4);
        var evento = this.repo.getEvento(e4Id);
        assertTrue(evento.isPresent());
        assertEquals(evento.get(), e4);
    }

    @Test
    void eliminarEvento() {
        this.repo.eliminarEvento(this.e3);
        assertTrue(this.repo.getEvento(this.e3.getId()).isEmpty());
    }
}