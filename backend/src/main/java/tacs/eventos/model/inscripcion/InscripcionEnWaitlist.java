package tacs.eventos.model.inscripcion;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tacs.eventos.model.Usuario;

import java.time.LocalDateTime;

/**
 * Representa un ítem en la waitlist de un evento. O sea, una inscripción que está en la waitlist, que luego podrá o no
 * dar lugar a una inscripción confirmada.
 */
@RequiredArgsConstructor
@Getter
public class InscripcionEnWaitlist {
    // TODO: no sé si vamos a tener este problema, pero probablemente querramos tener cargado en memoria solamente el
    // usuarioId. Eso capaz se resuelva con una estrategia de búsqueda Lazy en la base de datos.
    private final Usuario candidato;
    private final LocalDateTime fechaIngreso = LocalDateTime.now();
}
