package tacs.eventos.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tacs.eventos.model.Evento;
import tacs.eventos.repository.evento.EventosRepository;

@SpringBootTest
class EventosInMemoryRepoTest {
    @Autowired
    private EventosRepository repo;

    private Evento e1;
    private Evento e2;
    private Evento e3;

    @BeforeEach
    void setUp() {
        this.e1 = new Evento("Evento 1", "", null, 1, "", 0, 0, "Deporte");
        this.e2 = new Evento("Evento 2", "", null, 1, "", 0, 0, "Moda");
        this.e3 = new Evento("Evento 3", "", null, 1, "", 0, 0, "Deporte");
        this.repo.save(e1);
        this.repo.save(e2);
        this.repo.save(e3);
    }

    @AfterEach
    void setUp2() {
        this.repo.delete(e1);
        this.repo.delete(e2);
        this.repo.delete(e3);
    }

    @Test
    void todos() {
        var eventos = this.repo.findAll();
        assertEquals(3, eventos.size());
        assertTrue(eventos.contains(this.e1));
        assertTrue(eventos.contains(this.e2));
        assertTrue(eventos.contains(this.e3));
    }

    @Test
    void getEvento() {
        var evento = this.repo.findById(this.e2.getId());
        assertTrue(evento.isPresent());
        assertEquals(this.e2, evento.get());
    }

    @Test
    void getEventosPorOrganizador() {
        return;
    }

    @Test
    void getEventosPorCategoria() {
        var eventosDeporte = this.repo.findByCategoria("Deporte");
        var eventosModa = this.repo.findByCategoria("Moda");

        assertTrue(eventosDeporte.contains(this.e1));
        assertTrue(eventosDeporte.contains(this.e3));

        assertTrue(eventosModa.contains(this.e2));
    }

    @Test
    void guardarEvento() {
        var e4 = new Evento("Evento 4", "", null, 1, "", 0, 0, "Deporte");
        var e4Id = e4.getId();

        this.repo.save(e4);
        var evento = this.repo.findById(e4Id);
        assertTrue(evento.isPresent());
        assertEquals(evento.get(), e4);
        this.repo.delete(e4);
    }

    @Test
    void eliminarEvento() {
        this.repo.delete(this.e3);
        assertTrue(this.repo.findById(this.e3.getId()).isEmpty());
    }
}
