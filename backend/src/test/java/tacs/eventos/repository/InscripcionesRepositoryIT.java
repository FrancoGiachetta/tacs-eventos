// package tacs.eventos.repository;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
// import org.springframework.test.context.ActiveProfiles;
// import org.springframework.test.context.TestPropertySource;
// import tacs.eventos.model.Evento;
// import tacs.eventos.model.Usuario;
// import tacs.eventos.model.inscripcion.EstadoInscripcion;
// import tacs.eventos.model.inscripcion.InscripcionEvento;
// import tacs.eventos.model.inscripcion.InscripcionFactory;
// import tacs.eventos.repository.inscripcion.InscripcionesRepository;
//
// import java.time.LocalDateTime;
// import java.util.List;
// import java.util.Optional;
//
// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertTrue;
//
/// / TODO: este fue mi intento de hacer un test del repo pegandole directamente a una instancia de Mongo. Los tests
// individuales están bien, pero no logré hacer funcionar la configuración para pegarle a Mongo. Si no lo logramos hacer
// funcionar fácil, borrar la clase entera y dejar el repo sin tests unitarios. De todas formas no tiene queries
// escritos a mano. Son todos generados autimáticamente por Sptring Boot a partir de los nombres de los métodos.
/// / TODO: no commiteé los cambios en el POM, habría que agregar las dependencias.
// @DataMongoTest
// @ActiveProfiles("test")
// @TestPropertySource(properties = {
// "spring.mongodb.embedded.version=7.0.14"
// })
// class InscripcionesRepositoryIT {
//
// @Autowired
// private InscripcionesRepository repository;
//
// private Usuario u1;
// private Usuario u2;
// private Evento e1;
// private Evento e2;
//
// @BeforeEach
// void setUp() {
// repository.deleteAll();
//
// u1 = new Usuario("u1@example.com", "pwd", null);
// u2 = new Usuario("u2@example.com", "pwd", null);
//
// e1 = new Evento("Concierto", "desc", LocalDateTime.now().plusDays(5), 120, "Teatro", 100, 50.0, "Musica");
// e2 = new Evento("Taller", "desc", LocalDateTime.now().plusDays(10), 180, "Centro", 30, 10.0, "Educacion");
// }
//
// @Test
// void noCanceladaParaParticipanteYEvento_devuelveLaInscripcionNoCancelada() {
// // confirmada
// InscripcionEvento c = InscripcionFactory.confirmada(u1, e1);
// repository.save(c);
//
// // cancelada (mismo usuario y evento) para asegurar que no se elija esta
// InscripcionEvento x = InscripcionFactory.confirmada(u1, e1);
// x.cancelar();
// repository.save(x);
//
// Optional<InscripcionEvento> res = repository.noCanceladaParaParticipanteYEvento(u1, e1);
//
// assertTrue(res.isPresent());
// assertEquals(c.getId(), res.get().getId());
// assertTrue(res.get().estaConfirmada());
// }
//
// @Test
// void findByParticipanteAndEstadoNot_excluyeCanceladas() {
// // confirmada y pendiente
// InscripcionEvento c = InscripcionFactory.confirmada(u1, e1);
// InscripcionEvento p = InscripcionFactory.pendiente(u1, e2);
// // cancelada
// InscripcionEvento x = InscripcionFactory.confirmada(u1, e1);
// x.cancelar();
//
// repository.save(c);
// repository.save(p);
// repository.save(x);
//
// List<InscripcionEvento> res = repository.findByParticipanteAndEstadoNot(u1, EstadoInscripcion.CANCELADA);
// assertEquals(2, res.size());
// assertTrue(res.stream().anyMatch(i -> i.getId().equals(c.getId())));
// assertTrue(res.stream().anyMatch(i -> i.getId().equals(p.getId())));
// assertTrue(res.stream().noneMatch(InscripcionEvento::estaCancelada));
// }
//
// @Test
// void findByEventoAndEstado_filtraPorEstado() {
// InscripcionEvento p1 = InscripcionFactory.pendiente(u1, e1);
// InscripcionEvento p2 = InscripcionFactory.pendiente(u2, e1);
// InscripcionEvento c = InscripcionFactory.confirmada(u1, e1);
//
// repository.save(p1);
// repository.save(p2);
// repository.save(c);
//
// List<InscripcionEvento> pendientes = repository.findByEventoAndEstado(e1, EstadoInscripcion.PENDIENTE);
// assertEquals(2, pendientes.size());
// assertTrue(pendientes.stream().allMatch(InscripcionEvento::estaPendiente));
// }
//
// @Test
// void findByEventoAndEstadoOrderByfechaHoraIngresoAWaitlist_ordenaAscPorIngresoAWL() throws Exception {
// InscripcionEvento p1 = InscripcionEvento.crearNueva(u1, e1, LocalDateTime.of(2025, 9, 28, 23, 45, 30), null,
// EstadoInscripcion.PENDIENTE);
// repository.save(p1);
//
// InscripcionEvento p2 = InscripcionEvento.crearNueva(u1, e1, LocalDateTime.of(2026, 9, 28, 23, 45, 30), null,
// EstadoInscripcion.PENDIENTE);
// ;
// repository.save(p2);
//
// List<InscripcionEvento> ordenadas = repository.findByEventoAndEstadoOrderByfechaHoraIngresoAWaitlist(
// e1, EstadoInscripcion.PENDIENTE
// );
//
// assertEquals(2, ordenadas.size());
// // p1 debe ser anterior a p2
// assertEquals(p1.getId(), ordenadas.get(0).getId());
// assertEquals(p2.getId(), ordenadas.get(1).getId());
// }
//
// @Test
// void countByEventoAndEstado_cuentaSoloPorEstado() {
// // 2 confirmadas y 1 pendiente en e1
// var i1 = InscripcionFactory.confirmada(u1, e1);
// i1.cancelar();
// repository.save(i1);
// repository.save(InscripcionFactory.confirmada(u2, e1));
// repository.save(InscripcionFactory.confirmada(u1, e1));
//
// int confirmadas = repository.countByEventoAndEstado(e1, EstadoInscripcion.CONFIRMADA);
// assertEquals(2, confirmadas);
//
// int pendientes = repository.countByEventoAndEstado(e1, EstadoInscripcion.PENDIENTE);
// assertEquals(1, pendientes);
// }
//
// @Test
// void countByEvento_cuentaTodasLasInscripcionesDelEvento() {
// // En e1: 1 confirmada, 1 pendiente, 1 cancelada
// InscripcionEvento c = InscripcionFactory.confirmada(u1, e1);
// InscripcionEvento p = InscripcionFactory.pendiente(u2, e1);
// InscripcionEvento x = InscripcionFactory.confirmada(u1, e1);
// x.cancelar();
//
// repository.save(c);
// repository.save(p);
// repository.save(x);
//
// int total = repository.countByEvento(e1);
// assertEquals(3, total);
// }
//
// @Test
// void noCanceladasDeParticipante_devuelveConfirmadasYPendientesDelUsuario() {
// InscripcionEvento c1 = InscripcionFactory.confirmada(u1, e1);
// InscripcionEvento p1 = InscripcionFactory.pendiente(u1, e2);
// InscripcionEvento x1 = InscripcionFactory.confirmada(u1, e1);
// x1.cancelar();
//
// repository.save(c1);
// repository.save(p1);
// repository.save(x1);
//
// List<InscripcionEvento> res = repository.noCanceladasDeParticipante(u1);
// assertEquals(2, res.size());
// assertTrue(res.stream().anyMatch(i -> i.getId().equals(c1.getId())));
// assertTrue(res.stream().anyMatch(i -> i.getId().equals(p1.getId())));
// assertTrue(res.stream().noneMatch(InscripcionEvento::estaCancelada));
// }
// }
