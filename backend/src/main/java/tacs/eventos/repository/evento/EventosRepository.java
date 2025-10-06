package tacs.eventos.repository.evento;

import tacs.eventos.model.evento.Evento;
import tacs.eventos.repository.FiltroBusqueda;

import java.util.List;
import java.util.Optional;

public interface EventosRepository {

    /**
     * @return todos los eventos
     */
    List<Evento> findAll();

    /**
     * @param eventoId
     * @return evento con ese id, u Optional.empty() si no existe
     */
    Optional<Evento> findById(String eventoId);

    // TODO: si vamos a terminar modelando usuarios cambiarlo a la entidad Usuario

    /**
     * @param organizadorId
     * @return los eventos cuyo organizador es el usuario pasado por parámetro
     */
    List<Evento> findByOrganizador(String organizadorId);

    /**
     * @param categoria
     * @return los eventos que pertenecen a esa cateogoria
     */
    List<Evento> findByCategoria(String categoria);

    /**
     * Elimina el evento, si es que existe
     *
     * @param evento
     */
    void delete(Evento evento);

    long count();

    void save(Evento evento);

    List<Evento> findByFiltroBusqueda(List<FiltroBusqueda<Evento>> filtrosBusqueda);

}
