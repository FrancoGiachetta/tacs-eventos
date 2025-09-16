package tacs.eventos.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class CreacionEventoRequest {
    @NotNull
    @Length(min = 3, max = 100)
    private String titulo;
    @NotNull
    @Length(min = 10, max = 1000)
    private String descripcion;
    @NotNull
    private LocalDateTime fechaHoraInicio;
    @NotNull
    @Positive
    private int duracionMinutos;
    @NotBlank
    @Length(min = 3, max = 300)
    private String ubicacion;
    @NotNull
    @Positive
    private int cupoMaximo;
    @NotNull
    @Min(0)
    private double precio;
    @NotNull
    @Length(max = 100)
    private String categoria;
}
