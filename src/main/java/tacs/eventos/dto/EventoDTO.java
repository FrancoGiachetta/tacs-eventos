package tacs.eventos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record EventoDTO(@NotBlank String titulo, @NotBlank String descripcion, @NotNull LocalDateTime fechaHoraInicio,
                        @NotNull int duracionMinutos, @NotBlank String ubicacion, @NotNull int cupoMaximo,
                        @NotNull double precio,
                        @NotBlank String categoria) {
}
