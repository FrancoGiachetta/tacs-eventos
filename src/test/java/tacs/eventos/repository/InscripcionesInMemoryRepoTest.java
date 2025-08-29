package tacs.eventos.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tacs.eventos.model.Evento;
import tacs.eventos.repository.inscripcion.InscripcionesInMemoryRepo;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.inscripcion.InscripcionEvento;
import tacs.eventos.model.inscripcion.InscripcionFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InscripcionesInMemoryRepoTest {
    private InscripcionesInMemoryRepo repo;
    private Evento e;
    private InscripcionEvento i1;
    private InscripcionEvento i2;
    private InscripcionEvento i3;
    private Usuario u1;
    private Usuario u2;
    private Usuario u3;
    private Usuario u4;

    @BeforeEach
    void setUp() {
        this.repo = new InscripcionesInMemoryRepo();
        this.e = new Evento("Evento", "", null, 1, "", 0, 0, "Deporte");
        this.u1 = new Usuario("pepe@gmail.com", "asd", null);
        this.u2 = new Usuario("pepe@gmail.com", "asd", null);
        this.u3 = new Usuario("pepe@gmail.com", "asd", null);
        this.u4 = new Usuario("pepe@gmail.com", "asd", null);
        this.i1 = InscripcionFactory.directa(u1, this.e);
        this.i2 = InscripcionFactory.directa(u2, this.e);
        this.i3 = InscripcionFactory.directa(u3, this.e);
        this.repo.guardarInscripcion(this.i1);
        this.repo.guardarInscripcion(this.i2);
        this.repo.guardarInscripcion(this.i3);
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
        var inscripcion = this.repo.getInscripcion(u1, this.e);
        assertTrue(inscripcion.isPresent());
        assertEquals(this.i1, inscripcion.get());
    }

    @Test
    void getInscripcionesPorParticipante() {
        var inscripciones = this.repo.getInscripcionesPorParticipante(u1);
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
        var i4 = InscripcionFactory.directa(u4, this.e);
        this.repo.guardarInscripcion(i4);
        var i = this.repo.getInscripcion(u4, this.e);
        assertTrue(i.isPresent());
        assertEquals(i.get(), i4);
    }

    @Test
    void eliminarInscripcion() {
        this.repo.eliminarInscripcion(this.i2);
        var i = this.repo.getInscripcion(u4, this.e);
        assertTrue(i.isEmpty());
    }
}