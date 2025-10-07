package tacs.eventos.repository.evento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import tacs.eventos.model.evento.Evento;
import tacs.eventos.repository.FiltroBusqueda;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class EventosRepositoryImpl implements EventosRepository {

    @Autowired
    private MongoTemplate eventos;

    @Override
    public List<Evento> findAll() {
        return eventos.findAll(Evento.class);

    }

    @Override
    public Optional<Evento> findById(String eventoId) {
        return Optional.ofNullable(eventos.findById(eventoId, Evento.class));
    }

    @Override
    public List<Evento> findByOrganizador(String organizadorId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("organizador").is(organizadorId));
        return new ArrayList<>(eventos.find(query, Evento.class));

    }

    @Override
    public List<Evento> findByFiltroBusqueda(List<FiltroBusqueda<Evento>> filtrosBusqueda) {
        List<Evento> todos = eventos.findAll(Evento.class);
        return todos.stream().filter(e -> filtrosBusqueda.stream().allMatch(f -> f.aplicarCondicionfiltrado(e)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Evento> findByCategoria(String categoria) {
        Query query = new Query();
        query.addCriteria(Criteria.where("categoria").is(categoria));
        return new ArrayList<>(eventos.find(query, Evento.class));

    }

    @Override
    public void delete(Evento evento) {
        this.eventos.remove(evento);
    }

    public long count() {

        return eventos.count(new Query(), Evento.class);

    }

    @Override
    public void save(Evento evento) {
        this.eventos.save(evento);
    }

}