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
    public Optional<InscripcionEvento> getInscripcion(Usuario participante, Evento evento) {
        return this.inscripciones.stream()
                .filter(i -> i.getEvento().equals(evento) && i.getParticipante().equals(participante)).findFirst();
    }

    @Override
    public List<InscripcionEvento> getInscripcionesPorParticipante(Usuario participante) {
        return this.inscripciones.stream().filter(i -> i.getParticipante().equals(participante)).toList();
    }

    @Override
    public List<InscripcionEvento> getInscripcionesPorEvento(Evento evento) {
        return this.inscripciones.stream().filter(i -> i.getEvento().equals(evento)).toList();
    }

    @Override
    public void guardarInscripcion(InscripcionEvento inscripcion) {
        if (!this.inscripciones.contains(inscripcion)) {
            this.inscripciones.add(inscripcion);
        }
    }

    @Override
    public void eliminarInscripcion(InscripcionEvento inscripcion) {
        this.inscripciones.remove(inscripcion);
    }

    @Override
    public int cantidadInscriptos(Evento evento) {
        /*
         * Si esto llega a no ser performante, podemos guardar las inscripciones en un diccionario indexado por Evento y
         * Fecha de Confirmación, para que sea más rápido acceder por esos campos. O incluso armar un cache de cantidad
         * de inscriptos en un diccionario, implementado como un hashmap <Evento, cantidad> que se guarde y actualice
         * adentro de este repo.
         */
        return (int) this.inscripciones.stream()
                .filter(i -> i.getEvento().equals(evento) && i.getEstado() == EstadoInscripcion.CONFIRMADA).count();
    }
}
