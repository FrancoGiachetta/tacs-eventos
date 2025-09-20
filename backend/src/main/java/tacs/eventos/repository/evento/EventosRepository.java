package tacs.eventos.repository.evento;

import org.springframework.data.mongodb.repository.MongoRepository;
import tacs.eventos.model.Evento;

import java.util.List;
import java.util.Optional;

public interface EventosRepository extends MongoRepository<Evento, String>,EventosRepositoryCustom {

    /**
     * @return todos los eventos
     */
    List<Evento> findAll();

    /**
     * @param eventoId
     *
     * @return evento con ese id, u Optional.empty() si no existe
     */
    Optional<Evento> findById(String eventoId);

    // TODO: si vamos a terminar modelando usuarios cambiarlo a la entidad Usuario

    /**
     * @param organizadorId
     *
     * @return los eventos cuyo organizador es el usuario pasado por parámetro
     */
    List<Evento> findByOrganizador(String organizadorId);


    //List<Evento> findByFiltroBusqueda(List<FiltroBusqueda<Evento>> filtrosBusqueda);

    /**
     * @param categoria
     *
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

}
