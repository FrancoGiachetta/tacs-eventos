package tacs.eventos.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Document(collection = "eventos")
public class Evento {
    @Setter
    @Indexed
    private String id;
    @Setter
    @Indexed
    private String titulo;
    @Setter
    private String descripcion;
    @Setter
    private LocalDateTime fechaHoraInicio;
    @Setter
    private int duracionMinutos;
    @Setter
    private String ubicacion;
    @Setter
    private int cupoMaximo;
    @Setter
    @Indexed
    private double precio; // TODO: cambiar el tipo de dato. Debería ser un número de precisión fija. Fijarme si no
    // puedo recibir directamente BigDecimal o algo así en el DTO. Si no, que el front mande un
    // String que cumpla la regex correcta, y el back lo transforme manualmente.
    @Setter
    @Indexed
    private String categoria;
    private boolean abierto = true;

    @Setter
    private Usuario organizador;

    public Evento(String id, String titulo, String descripcion, LocalDateTime fechaHoraInicio, int duracionMinutos,
            String ubicacion, int cupoMaximo, double precio, String categoria, boolean abierto, Usuario organizador) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaHoraInicio = fechaHoraInicio;
        this.duracionMinutos = duracionMinutos;
        this.ubicacion = ubicacion;
        this.cupoMaximo = cupoMaximo;
        this.precio = precio;
        this.categoria = categoria;
        this.abierto = abierto;
        this.organizador = organizador;
    }

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
    public boolean permiteIncripcion(int inscritos) {
        return abierto && (inscritos < cupoMaximo);
    }

    /**
     * Marca el evento como cerrado, impidiendo nuevas inscripciones.
     */
    public void cerrarEvento() {
        this.abierto = false;
    }

    public void abrirEvento() {
        this.abierto = true;
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
