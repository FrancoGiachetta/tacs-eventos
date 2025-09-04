package tacs.eventos.repository.evento.busqueda;

import lombok.AllArgsConstructor;
import tacs.eventos.model.Evento;
import tacs.eventos.repository.FiltroBusqueda;

@AllArgsConstructor
public class FiltradoPorPrecio implements FiltroBusqueda<Evento> {
    private Double precioMinimo;
    private Double precioMaximo;

    @Override
    public Boolean aplicarCondicionfiltrado(Evento evento) {
        double precio = evento.getPrecio();
        return precio >= precioMinimo && precio <= precioMaximo;
    }
}
