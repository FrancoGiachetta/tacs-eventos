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
        if (precioMinimo == null && precioMaximo == null) {
            return true;
        }
        
        double precio = evento.getPrecio();
        boolean cumpleMinimo = precioMinimo == null || precio >= precioMinimo;
        boolean cumpleMaximo = precioMaximo == null || precio <= precioMaximo;
        
        return cumpleMinimo && cumpleMaximo;
    }
}
