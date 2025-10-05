package tacs.eventos.repository.inscripcion;

import org.springframework.stereotype.Repository;
import tacs.eventos.model.Evento;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.inscripcion.EstadoInscripcion;
import tacs.eventos.model.inscripcion.InscripcionEvento;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("inscripcionesInMemoryRepo")
public class InscripcionesInMemoryRepo implements InscripcionesRepository {
    private final List<InscripcionEvento> inscripciones;

    public InscripcionesInMemoryRepo() {
        this.inscripciones = new ArrayList<>();
    }

    @Override
    public List<InscripcionEvento> todos() {
        return this.inscripciones;
    }

    @Override
    public Optional<InscripcionEvento> getInscripcionConfirmada(Usuario participante, Evento evento) {
        return this.inscripciones.stream()
                .filter(i -> i.getEvento().equals(evento) && i.getParticipante().equals(participante))
                .filter(i -> i.getEstado() == EstadoInscripcion.CONFIRMADA).findFirst();
    }

    @Override
    public List<InscripcionEvento> getInscripcionesNoCanceladasPorParticipante(Usuario participante) {
        return this.inscripciones.stream().filter(i -> i.getParticipante().equals(participante))
                .filter(i -> i.getEstado() != EstadoInscripcion.CANCELADA).toList();
    }

    @Override
    public List<InscripcionEvento> getInscripcionesPorEvento(Evento evento) {
        return this.inscripciones.stream().filter(i -> i.getEvento().equals(evento)).toList();
    }

    @Override
    public void guardarInscripcion(InscripcionEvento inscripcion) {
        // Simplemente agregamos la inscripción - puede haber múltiples estados para el mismo usuario/evento
        this.inscripciones.add(inscripcion);
    }

    @Override
    public int cantidadInscriptos(Evento evento) {
        /*
         * Si esto llega a no ser performante, podemos guardar las inscripciones en un diccionario indexado por Evento y
         * Fecha de Confirmación, para que sea más rápido acceder por esos campos. O incluso armar un cache de cantidad
         * de inscriptos en un diccionario, implementado como un hashmap <Evento, cantidad> que se guarde y actualice
         * adentro de este repo.
         */
        long cantidad = this.inscripciones.stream()
                .filter(i -> i.getEvento().equals(evento) && i.getEstado() == EstadoInscripcion.CONFIRMADA).count();

        System.out.println("DEBUG cantidadInscriptos - Evento: " + evento.getId() + " | Cantidad: " + cantidad);
        return (int) cantidad;
    }

    @Override
    public Optional<InscripcionEvento> getInscripcionParaUsuarioYEvento(Usuario usuarioInscripto, Evento evento) {
        return this.inscripciones.stream()
                .filter(i -> i.getEvento().equals(evento) && i.getParticipante().equals(usuarioInscripto))
                .filter(i -> i.getEstado() != EstadoInscripcion.CANCELADA)
                .findFirst();
    }

    @Override
    public Optional<InscripcionEvento> getInscripcionPorId(String id) {
        return this.inscripciones.stream().filter(i -> i.getId().equals(id)).findFirst();
    }

    @Override
    public List<InscripcionEvento> getInscripcionesPendientes(Evento evento) {
        return this.inscripciones.stream().filter(i -> i.getEvento().equals(evento))
                .filter(InscripcionEvento::estaPendiente).toList();
    }
}
