package tacs.eventos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class CreacionEventoRequest {
    @NotBlank
    private String titulo;
    @NotBlank
    private String descripcion;
    @NotNull
    private LocalDateTime fechaHoraInicio;
    @NotNull
    private int duracionMinutos;
    @NotBlank
    private String ubicacion;
    @NotNull
    private int cupoMaximo;
    @NotNull
    private double precio;
    @NotBlank
    private String categoria;
}
