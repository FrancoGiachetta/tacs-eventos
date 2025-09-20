package tacs.eventos.repository.evento;

import tacs.eventos.model.Evento;
import tacs.eventos.repository.FiltroBusqueda;
import java.util.List;

/**
 * Filtra los eventos según los filtros de búsqueda proporcionados.
 *
 *
 *            Lista de filtros de búsqueda a aplicar.
 *
 * @return Lista de eventos que cumplen con todos los filtros de búsqueda.
 */
public interface EventosRepositoryCustom {
    List<Evento> findByFiltroBusqueda(List<FiltroBusqueda<Evento>> filtrosBusqueda);
}
