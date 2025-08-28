package tacs.eventos.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Getter
public class InscripcionEnWaitlist {
    // TODO: no sé si vamos a tener este problema, pero probablemente querramos tener cargado en memoria solamente el
    // usuarioId. Eso capaz se resuelva con una estrategia de búsqueda Lazy en la base de datos.
    private final Usuario candidato;
    private final LocalDateTime fechaIngreso = LocalDateTime.now();
}
