package tacs.eventos.repository.evento.busqueda;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import tacs.eventos.model.evento.Evento;
import tacs.eventos.repository.FiltroBusqueda;

import java.time.LocalDate;

@AllArgsConstructor
public class FiltradoPorFechaInicio implements FiltroBusqueda<Evento> {
    @NotNull
    private LocalDate fechaMinima;
    @NotNull
    private LocalDate fechaMaxima;

    @Override
    public Boolean aplicarCondicionfiltrado(Evento evento) {
        LocalDate fechaInicio = LocalDate.from(evento.getFechaHoraInicio());
        // Chequea que fecha de inicio >= fecha minima y la fecha maxima >= fecha de inicio.
        return (fechaInicio.compareTo(fechaMinima) * fechaMaxima.compareTo(fechaInicio)) >= 0;
    }
}
