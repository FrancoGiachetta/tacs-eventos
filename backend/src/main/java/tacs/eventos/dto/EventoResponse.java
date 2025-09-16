package tacs.eventos.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class EventoResponse extends CreacionEventoRequest {
    private String id;
    private boolean abierto;
}
