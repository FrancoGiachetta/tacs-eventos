package tacs.eventos.repository.evento;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import tacs.eventos.model.Evento;
import tacs.eventos.repository.FiltroBusqueda;
import java.util.List;
import java.util.stream.Collectors;

public class EventosRepositoryImpl implements EventosRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Evento> findByFiltroBusqueda(List<FiltroBusqueda<Evento>> filtrosBusqueda) {
        List<Evento> todos = mongoTemplate.findAll(Evento.class);
        return todos.stream()
                .filter(e -> filtrosBusqueda.stream().allMatch(f -> f.aplicarCondicionfiltrado(e)))
                .collect(Collectors.toList());
    }

}
