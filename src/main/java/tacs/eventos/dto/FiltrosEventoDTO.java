package tacs.eventos.dto;

import jakarta.validation.constraints.Future;
import tacs.eventos.model.Evento;
import tacs.eventos.repository.FiltroBusqueda;

import jakarta.validation.constraints.FutureOrPresent;
import tacs.eventos.repository.evento.busqueda.FiltradoPorCategoria;
import tacs.eventos.repository.evento.busqueda.FiltradoPorFechaInicio;
import tacs.eventos.repository.evento.busqueda.FiltradoPorPalabrasClave;
import tacs.eventos.repository.evento.busqueda.FiltradoPorPrecio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record FiltrosEventoDTO(@FutureOrPresent Optional<LocalDate> fechaMinima,
        @Future Optional<LocalDate> fechaMaxima, Optional<String> categoria,
        // TODO: Ahora la ubicacion de un Evento es un String. Esto deberia ser una clase.
        // Ubicacion ubicacion
        Optional<Double> precioMinimoPesos, Optional<Double> precioMaximoPesos, List<String> palabrasClave) {

    public List<FiltroBusqueda<Evento>> toListFiltroBusqueda() {
        List<FiltroBusqueda<Evento>> filtros = new ArrayList<>();

        LocalDate fechaMin = fechaMinima.orElse(LocalDate.now());
        LocalDate fechaMax = fechaMaxima.orElse(LocalDate.MAX);

        filtros.add(new FiltradoPorFechaInicio(fechaMin, fechaMax));

        Double precioMin = precioMinimoPesos.orElse(0.0);
        Double precioMax = precioMaximoPesos.orElse(Double.MAX_VALUE);

        filtros.add(new FiltradoPorPrecio(precioMin, precioMax));

        categoria.ifPresent(c -> filtros.add(new FiltradoPorCategoria(c)));

        if (!palabrasClave.isEmpty()) {
            filtros.add(new FiltradoPorPalabrasClave(palabrasClave));
        }

        return filtros;
    }
}
