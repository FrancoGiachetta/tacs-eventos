package tacs.eventos.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import tacs.eventos.config.TestMongoConfiguration;
import tacs.eventos.config.TestRedisConfiguration;
import tacs.eventos.model.evento.Evento;
import tacs.eventos.repository.evento.EventosRepository;
import tacs.eventos.repository.evento.EventosRepositoryImpl;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
@Import({TestMongoConfiguration.class, TestRedisConfiguration.class})
@ActiveProfiles("test")
class EventosRepoTest { // Testea contra la base real
    @Autowired
    private EventosRepository repo;

    @MockBean
    private RedissonClient redissonClient;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private EventosRepositoryImpl eventosRepository; // Tu implementación con MongoTemplate

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    private Evento e1;
    private Evento e2;
    private Evento e3;

    @Test
    void testConexion() {
        assertNotNull(mongoTemplate);
        // Verificar que podemos realizar operaciones
        mongoTemplate.getDb().getName();
    }

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
