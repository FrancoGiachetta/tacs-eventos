package tacs.eventos.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import tacs.eventos.model.Evento;
import tacs.eventos.repository.FiltroBusqueda;

import jakarta.validation.constraints.FutureOrPresent;
import tacs.eventos.repository.evento.busqueda.FiltradoPorCategoria;
import tacs.eventos.repository.evento.busqueda.FiltradoPorFechaInicio;
import tacs.eventos.repository.evento.busqueda.FiltradoPorPalabrasClave;
import tacs.eventos.repository.evento.busqueda.FiltradoPorPrecio;

import java.beans.ConstructorProperties;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ToString
@Data
@NoArgsConstructor
public class FiltrosEventoDTO {
    @FutureOrPresent
    LocalDate fechaMinima;
    @Future
    LocalDate fechaMaxima;
    String categoria;
    // TODO: Ahora la ubicacion de un Evento es un String. Esto deberia ser una clase.
    // Ubicacion ubicacion
    @Min(0)
    Double precioMinimoPesos;

    Double precioMaximoPesos;
    List<String> palabrasClave;

    @ConstructorProperties({ "fecha-inicio-minima", "fecha-inicio-maxima", "categoria", "precio-minimo-pesos",
            "precio-maximo-pesos", "palabras-clave" })
    public FiltrosEventoDTO(LocalDate fechaMinima, LocalDate fechaMaxima, String categoria,
            // Ubicacion ubicacion
            Double precioMinimoPesos, Double precioMaximoPesos, List<String> palabrasClave) {
        this.fechaMinima = fechaMinima != null ? fechaMinima : LocalDate.now();
        this.fechaMaxima = fechaMaxima != null ? fechaMinima : LocalDate.MAX;
        this.precioMinimoPesos = precioMinimoPesos != null ? precioMinimoPesos : 0.0;
        this.precioMaximoPesos = precioMaximoPesos != null ? precioMaximoPesos : Double.MAX_VALUE;
        this.categoria = categoria;
        this.palabrasClave = palabrasClave != null ? palabrasClave : new ArrayList<>();
    }

    public List<FiltroBusqueda<Evento>> toListFiltroBusqueda() {
        List<FiltroBusqueda<Evento>> filtros = new ArrayList<>();

        filtros.add(new FiltradoPorFechaInicio(this.fechaMinima, this.fechaMaxima));
        filtros.add(new FiltradoPorPrecio(this.precioMinimoPesos, this.precioMaximoPesos));

        if (!this.categoria.isEmpty()) {
            filtros.add(new FiltradoPorCategoria(this.categoria));
        }

        if (!this.palabrasClave.isEmpty()) {
            filtros.add(new FiltradoPorPalabrasClave(this.palabrasClave));
        }

        return filtros;
    }
}
