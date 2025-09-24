package tacs.eventos.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tacs.eventos.dto.EventoResponse;
import tacs.eventos.model.evento.EstadoEvento;
import tacs.eventos.model.evento.Evento;

@Configuration
public class ModelMapperConfig {

    /**
     * Configura el ModelMapper. No se necesita agregar el mapeo de las entidades triviales que tengan los mismos
     * nombres de atributos, porque lo resuelve solo por reflection. Solamente agregar los mapeos para las
     * transformaciones que no estén funcionando bien automáticamente.
     *
     * @return el ModelMapper, una utilidad para mapear entre DTOs y entidades
     */
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        mapper.addMappings(new PropertyMap<Evento, EventoResponse>() {
            @Override
            protected void configure() {
                // All other fields will map automatically by default
                map().setAbierto(source.getEstado() == EstadoEvento.ABIERTO);
            }
        });

        return mapper;
    }
}
