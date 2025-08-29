package tacs.eventos.model.inscripcion;

import tacs.eventos.model.Evento;
import tacs.eventos.model.InscripcionEnWaitlist;
import tacs.eventos.model.Usuario;

import java.util.Optional;

public class InscripcionFactory {
    public static InscripcionEvento directa(Usuario participante, Evento evento) {
        return new InscripcionEvento(participante, evento, Optional.empty());
    }

    public static InscripcionEvento desdeWaitlist(Evento evento, InscripcionEnWaitlist inscripcionEnWaitlist) {
        return new InscripcionEvento(inscripcionEnWaitlist.getCandidato(), evento,
                Optional.of(inscripcionEnWaitlist.getFechaIngreso()));
    }
}
