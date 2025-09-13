package tacs.eventos.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class EventoResponseDTO {
    public String id;
    public String titulo;
    public String descripcion;
    public LocalDateTime fechaHoraInicio;
    public int duracionMinutos;
    public String ubicacion;
    public int cupoMaximo;
    public double precio;
    public String categoria;
    public boolean abierto;
}
