package tacs.eventos.config;

import org.modelmapper.Converter;
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

        // Convertidor para EstadoEvento a boolean
        Converter<EstadoEvento, Boolean> estadoEventoToBooleanConverter = ctx -> 
            ctx.getSource() == EstadoEvento.ABIERTO;

        mapper.addMappings(new PropertyMap<Evento, EventoResponse>() {
            @Override
            protected void configure() {
                // Map estado to abierto using the converter
                using(estadoEventoToBooleanConverter).map(source.getEstado()).setAbierto(false);
            }
        });

        return mapper;
    }
}
