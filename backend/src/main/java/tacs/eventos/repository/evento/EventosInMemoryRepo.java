package tacs.eventos.repository.evento;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import tacs.eventos.model.Evento;
import tacs.eventos.repository.FiltroBusqueda;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Optional;
import java.util.function.Function;
//TODO falta analizar mas el seteo de todos las operaciones a la base de datos
@Repository("eventosInMemoryRepo")
public class EventosInMemoryRepo implements EventosRepository {
    private final EventosRepository eventos;

    public EventosInMemoryRepo(EventosRepository personRepository) {
        this.eventos = personRepository;
    }

    @Override
    public List<Evento> findAll() {
        return eventos.findAll().stream().map(Evento::new).toList();

    }

    @Override
    public List<Evento> findByFiltroBusqueda(List<FiltroBusqueda<Evento>> filtrosBusqueda) {

        return eventos.findByFiltroBusqueda(filtrosBusqueda).stream().map(Evento::new).toList();
    }

    @Override
    public Optional<Evento> findById(String eventoId) {
        return eventos.findById(eventoId).map(Evento::new);
    }

    @Override
    public List<Evento> findByOrganizador(String organizadorId) {
        return eventos.findByOrganizador(organizadorId).stream().map(Evento::new).toList();
    }

    @Override
    public List<Evento> findByCategoria(String categoria) {
        return eventos.findByCategoria(categoria).stream().map(Evento::new).toList();
    }

    @Override
    public Evento save(Evento evento) {

        return eventos.save(evento);

    }

    @Override
    public void delete(Evento evento) {
        this.eventos.delete(evento);// remove(evento);
    }

    public long count() {
        return Math.toIntExact(this.eventos.count());// size();
    }



    @Override
    public <S extends Evento> S insert(S entity) {
        throw new UnsupportedOperationException("Método no implementado");
    }

    @Override
    public <S extends Evento> List<S> insert(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public <S extends Evento> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Evento> List<S> findAll(Example<S> example) {
        return List.of();
    }

    @Override
    public <S extends Evento> List<S> findAll(Example<S> example, Sort sort) {
        return List.of();
    }

    @Override
    public <S extends Evento> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Evento> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Evento> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Evento, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }



    @Override
    public <S extends Evento> List<S> saveAll(Iterable<S> entities) {
        return List.of();
    }

    @Override
    public boolean existsById(String s) {
        return false;
    }


    @Override
    public List<Evento> findAllById(Iterable<String> strings) {
        return List.of();
    }

    @Override
    public void deleteById(String s) {

    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {

    }

    @Override
    public void deleteAll(Iterable<? extends Evento> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Evento> findAll(Sort sort) {
        return List.of();
    }

    @Override
    public Page<Evento> findAll(Pageable pageable) {
        return null;
    }
}