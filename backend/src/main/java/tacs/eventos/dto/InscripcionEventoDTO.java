package tacs.eventos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import tacs.eventos.model.Evento;
import tacs.eventos.model.inscripcion.EstadoInscripcion;

@Data
@AllArgsConstructor
public class InscripcionEventoDTO {
    private Evento evento;
    private EstadoInscripcion estado;
}
