package tacs.eventos.repository.evento.busqueda;

import lombok.AllArgsConstructor;
import tacs.eventos.model.evento.Evento;
import tacs.eventos.repository.FiltroBusqueda;

@AllArgsConstructor
public class FiltradoPorCategoria implements FiltroBusqueda<Evento> {
    private String categoria;

    @Override
    public Boolean aplicarCondicionfiltrado(Evento evento) {
        if (categoria == null || categoria.trim().isEmpty()) {
            return true;
        }
        return evento.getCategoria().equals(categoria.trim());
    }
}
