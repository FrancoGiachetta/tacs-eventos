package tacs.eventos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import tacs.eventos.model.Evento;

import java.time.LocalDateTime;

public record EventoDTO(
        @NotBlank String titulo,
        @NotBlank String descripcion,
        @NotNull LocalDateTime fechaHoraInicio,
        @NotNull int duracionMinutos,
        @NotBlank String ubicacion,
        @NotNull int cupoMaximo,
        @NotNull double precio,
        @NotBlank String categoria
) {
    public static EventoDTO fromEntity(Evento evento) {
        return new EventoDTO(
                evento.getTitulo(),
                evento.getDescripcion(),
                evento.getFechaHoraInicio(),
                evento.getDuracionMinutos(),
                evento.getUbicacion(),
                evento.getCupoMaximo(),
                evento.getPrecio(),
                evento.getCategoria()
        );
    }
}
