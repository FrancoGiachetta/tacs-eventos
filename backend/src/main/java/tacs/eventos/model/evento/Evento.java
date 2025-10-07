package tacs.eventos.model.evento;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import tacs.eventos.model.Usuario;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED, onConstructor_ = @PersistenceCreator)
@Getter
@Document(collection = "eventos")
public class Evento {
    @Setter
    @Indexed
    private String id;
    @Setter
    @Indexed
    @NotBlank
    private String titulo;
    @Setter
    private String descripcion;
    @Setter
    private LocalDateTime fechaHoraInicio;
    @Setter
    @Positive
    private int duracionMinutos;
    @Setter
    private String ubicacion;
    @Setter
    @Positive
    private int cupoMaximo;
    @Setter
    @Indexed
    @Positive
    private double precio; // TODO: cambiar el tipo de dato. Debería ser un número de precisión fija. Fijarme si no
    // puedo recibir directamente BigDecimal o algo así en el DTO. Si no, que el front mande un
    // String que cumpla la regex correcta, y el back lo transforme manualmente.
    @Setter
    @Indexed
    private String categoria;
    private EstadoEvento estado = EstadoEvento.ABIERTO;

    @Setter
    private Usuario organizador;

    public Evento(String titulo, String descripcion, LocalDateTime fechaHoraInicio, int duracionMinutos,
            String ubicacion, int cupoMaximo, double precio, String categoria) {

        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaHoraInicio = fechaHoraInicio;
        this.duracionMinutos = duracionMinutos;
        this.ubicacion = ubicacion;
        this.cupoMaximo = cupoMaximo;
        this.precio = precio;
        this.categoria = categoria;
    }

    /**
     * Verifica si el evento permite nuevas inscripciones.
     *
     * @param inscritos
     *            Número actual de inscritos en el evento.
     *
     * @return true si el evento está abierto y no ha alcanzado el cupo máximo, false en caso contrario.
     */
    public boolean permiteInscripcion(int inscritos) {
        return estado == EstadoEvento.ABIERTO && (inscritos < cupoMaximo);
    }

    /**
     * Marca el evento como cerrado, impidiendo nuevas inscripciones.
     */
    public void cerrarEvento() {
        this.estado = EstadoEvento.CERRADO;
    }

    public void abrirEvento() {
        this.estado = EstadoEvento.ABIERTO;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Evento evento && evento.id != null && evento.id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
