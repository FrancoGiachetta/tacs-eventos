package tacs.eventos.dto;

import java.time.LocalDateTime;

public record EventoResponseDTO(String id, String titulo, String descripcion, LocalDateTime fechaHoraInicio,
        int duracionMinutos, String ubicacion, int cupoMaximo, double precio, String categoria, boolean abierto) {
}
