package tacs.eventos.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class Evento {
    private String id;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaHoraInicio;
    private int duracionMinutos;
    private String ubicacion;
    private int cupoMaximo;
    private double precio;
    private String categoria;
    private boolean abierto;

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
        this.abierto = true;
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

    @Override
    public boolean equals(Object o) {
        return o instanceof Evento evento && id.equals(evento.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
