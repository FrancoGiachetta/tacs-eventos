package tacs.eventos.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tacs.eventos.dto.EventoResponse;
import tacs.eventos.dto.UsuarioResponse;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.evento.EstadoEvento;
import tacs.eventos.model.evento.Evento;

import java.util.Optional;
import java.util.Set;

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
        Converter<EstadoEvento, Boolean> estadoEventoToBooleanConverter = ctx -> ctx
                .getSource() == EstadoEvento.ABIERTO;
        Converter<Usuario, UsuarioResponse> usuarioToUsuarioResponseConverter = ctx -> Optional
                .ofNullable(ctx.getSource()).map(u -> new UsuarioResponse(u.getId(), u.getEmail(), Set.of()))
                .orElse(null);

        mapper.addMappings(new PropertyMap<Evento, EventoResponse>() {
            @Override
            protected void configure() {
                // Map estado to abierto using the converter
                using(estadoEventoToBooleanConverter).map(source.getEstado()).setAbierto(false);
                using(usuarioToUsuarioResponseConverter).map(source.getOrganizador()).setOrganizador(null);
            }
        });

        return mapper;
    }
}
