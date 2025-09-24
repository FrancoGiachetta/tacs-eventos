package tacs.eventos.repository.evento;

import tacs.eventos.model.evento.Evento;
import tacs.eventos.repository.FiltroBusqueda;

import java.util.List;
import java.util.Optional;

public interface EventosRepository {

    /**
     * @return todos los eventos
     */
    List<Evento> todos();

    /**
     * @param eventoId
     *
     * @return evento con ese id, u Optional.empty() si no existe
     */
    Optional<Evento> getEvento(String eventoId);

    // TODO: si vamos a terminar modelando usuarios cambiarlo a la entidad Usuario

    /**
     * @param organizadorId
     *
     * @return los eventos cuyo organizador es el usuario pasado por parámetro
     */
    List<Evento> getEventosPorOrganizador(String organizadorId);

    /**
     * Filtra los eventos según los filtros de búsqueda proporcionados.
     *
     * @param filtrosBusqueda
     *            Lista de filtros de búsqueda a aplicar.
     *
     * @return Lista de eventos que cumplen con todos los filtros de búsqueda.
     */
    List<Evento> filtrarEventos(List<FiltroBusqueda<Evento>> filtrosBusqueda);

    /**
     * @param categoria
     *
     * @return los eventos que pertenecen a esa cateogoria
     */
    List<Evento> getEventosPorCategoria(String categoria);

    /**
     * Guarda el evento si este no está ya guardado.
     *
     * @param evento
     */
    void guardarEvento(Evento evento);

    /**
     * Elimina el evento, si es que existe
     *
     * @param evento
     */
    void eliminarEvento(Evento evento);

    int cantidaEventos();

}
