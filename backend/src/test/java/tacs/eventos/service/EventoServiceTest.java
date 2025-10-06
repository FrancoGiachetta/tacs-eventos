package tacs.eventos.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import tacs.eventos.config.TestMongoConfiguration;
import tacs.eventos.config.TestRedisConfiguration;
import tacs.eventos.model.evento.Evento;
import tacs.eventos.repository.FiltroBusqueda;
import tacs.eventos.repository.evento.EventosRepository;
import tacs.eventos.repository.evento.busqueda.FiltradoPorCategoria;
import tacs.eventos.repository.evento.busqueda.FiltradoPorFechaInicio;
import tacs.eventos.repository.evento.busqueda.FiltradoPorPalabrasClave;
import tacs.eventos.repository.evento.busqueda.FiltradoPorPrecio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import({TestRedisConfiguration.class, TestMongoConfiguration.class})
@ActiveProfiles("test")
@Testcontainers
public class EventoServiceTest {
    @Autowired
    EventoService eventoService;

    @Autowired
    private EventosRepository repo;

    private Evento e1;
    private Evento e2;
    private Evento e3;

    @BeforeEach
    void setUp() {
        this.e1 = new Evento("Evento Tennis", "Evento de comida, deporte y pasion",
                LocalDateTime.of(2025, 10, 10, 0, 0, 0), 1, "", 0, 1000, "Deporte");
        this.e2 = new Evento("Evento Ropa", "Evento de moda, desfile", LocalDateTime.of(2025, 11, 23, 0, 0, 0), 1, "",
                0, 100, "Moda");
        this.e3 = new Evento("Evento Computacion", "Evento de educacion, tecnica y aprensizaje",
                LocalDateTime.of(2025, 10, 23, 0, 0, 0), 1, "", 0, 10, "Educacion");

        eventoService.crearEvento(this.e1);
        eventoService.crearEvento(this.e2);
        eventoService.crearEvento(this.e3);
    }

    @AfterEach
    void setUp2() {
        this.repo.delete(e1);
        this.repo.delete(e2);
        this.repo.delete(e3);
    }

    @Test
    void crearEvento() {
        eventoService.crearEvento(this.e1);
        eventoService.crearEvento(this.e2);
        eventoService.crearEvento(this.e3);

        List<Evento> eventos = new ArrayList<>();
        eventos.add(this.e1);
        eventos.add(this.e2);
        eventos.add(this.e3);

        assertEquals(eventoService.listarEventos(), eventos);
    }

    @Test
    void buscarEventoPorPrecio() {
        eventoService.crearEvento(this.e1);
        eventoService.crearEvento(this.e2);
        eventoService.crearEvento(this.e3);

        List<FiltroBusqueda<Evento>> filtroBusqueda = new ArrayList<>();

        filtroBusqueda.add(new FiltradoPorPrecio(0.0, 100.0));

        List<Evento> eventosEsperados = new ArrayList<>();

        eventosEsperados.add(this.e2);
        eventosEsperados.add(this.e3);

        List<Evento> resultados = eventoService.filtrarEventos(filtroBusqueda);

        assertEquals(eventosEsperados, resultados);
    }

    @Test
    void buscarEventoPorCategoria() {
        eventoService.crearEvento(this.e1);
        eventoService.crearEvento(this.e2);
        eventoService.crearEvento(this.e3);

        List<FiltroBusqueda<Evento>> filtroBusqueda = new ArrayList<>();

        filtroBusqueda.add(new FiltradoPorCategoria("Moda"));

        List<Evento> eventosEsperados = new ArrayList<>();

        eventosEsperados.add(this.e2);

        List<Evento> resultados = eventoService.filtrarEventos(filtroBusqueda);

        assertEquals(eventosEsperados, resultados);
    }

    @Test
    void buscarEventoPorFecha() {
        eventoService.crearEvento(this.e1);
        eventoService.crearEvento(this.e2);
        eventoService.crearEvento(this.e3);

        List<FiltroBusqueda<Evento>> filtroBusqueda = new ArrayList<>();

        filtroBusqueda.add(new FiltradoPorFechaInicio(LocalDate.of(2025, 10, 10), LocalDate.of(2025, 10, 31)));

        List<Evento> eventosEsperados = new ArrayList<>();

        eventosEsperados.add(this.e1);
        eventosEsperados.add(this.e3);

        List<Evento> resultados = eventoService.filtrarEventos(filtroBusqueda);

        assertEquals(eventosEsperados, resultados);
    }

    @Test
    void buscarEventoPorPalbrasClave() {
        eventoService.crearEvento(this.e1);
        eventoService.crearEvento(this.e2);
        eventoService.crearEvento(this.e3);

        List<FiltroBusqueda<Evento>> filtroBusqueda = new ArrayList<>();
        List<String> palabrasClave = new ArrayList<>();

        palabrasClave.add("mODa");
        palabrasClave.add("coMpuTaCIon");

        filtroBusqueda.add(new FiltradoPorPalabrasClave(palabrasClave));

        List<Evento> eventosEsperados = new ArrayList<>();

        eventosEsperados.add(this.e2);
        eventosEsperados.add(this.e3);

        List<Evento> resultados = eventoService.filtrarEventos(filtroBusqueda);

        assertEquals(eventosEsperados, resultados);
    }
}
