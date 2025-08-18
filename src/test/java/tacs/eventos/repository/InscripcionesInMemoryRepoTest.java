package tacs.eventos.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tacs.eventos.model.Evento;
import tacs.eventos.model.InscripcionEvento;

class InscripcionesInMemoryRepoTest {
    private InscripcionesInMemoryRepo repo;
    private Evento e;
    private InscripcionEvento i1;
    private InscripcionEvento i2;
    private InscripcionEvento i3;

    @BeforeEach
    void setUp() {
        this.repo = new InscripcionesInMemoryRepo();
        this.e = new Evento(
            "Evento", "", null, 1, "", 0, 0, "Deporte"
        );
        this.i1 = new InscripcionEvento("1", this.e);
        this.i2 = new InscripcionEvento("2", this.e);
        this.i3 = new InscripcionEvento("3", this.e);
    }

    @Test
    void todos() {
        var inscripciones = this.repo.todos();
        assertTrue(inscripciones.contains(this.i1));
        assertTrue(inscripciones.contains(this.i2));
        assertTrue(inscripciones.contains(this.i3));
    }

    @Test
    void getInscripcion() {
        var inscripcion = this.repo.getInscripcion("1", this.e);
        assertTrue(inscripcion.isPresent());
        assertEquals(this.i1, inscripcion.get());
    }

    @Test
    void getInscripcionesPorParticipante() {
        var inscripciones = this.repo.getInscripcionesPorParticipante("1");
        assertEquals(1, inscripciones.size());
        assertTrue(inscripciones.contains(this.i1));
    }

    @Test
    void getInscripcionesPorEvento() {
        var inscripciones = this.repo.getInscripcionesPorEvento(this.e);
        assertTrue(inscripciones.contains(this.i1));
        assertTrue(inscripciones.contains(this.i2));
        assertTrue(inscripciones.contains(this.i3));
    }

    @Test
    void guardarInscripcion() {
        var i4 = new InscripcionEvento("4", this.e);
        var i = this.repo.getInscripcion("4", this.e);
        assertTrue(i.isPresent());
        assertEquals(i.get(), i4);
    }

    @Test
    void eliminarInscripcion() {
        this.repo.eliminarInscripcion(this.i2);
        var i = this.repo.getInscripcion("2", this.e);
        assertTrue(i.isEmpty());
    }
}