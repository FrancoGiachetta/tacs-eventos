package tacs.eventos.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tacs.eventos.dto.EventoDTO;
import tacs.eventos.model.Evento;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.createTypeMap(EventoDTO.class, Evento.class).setProvider(request -> {
            EventoDTO dto = (EventoDTO) request.getSource(); // casteo a EventoDTO
            return new Evento(dto.titulo(), dto.descripcion(), dto.fechaHoraInicio(), dto.duracionMinutos(),
                    dto.ubicacion(), dto.cupoMaximo(), dto.precio(), dto.categoria());
        });
        return mapper;
    }
}
