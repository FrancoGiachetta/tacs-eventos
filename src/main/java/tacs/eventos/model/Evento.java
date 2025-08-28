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

    public boolean permiteIncripcion(int inscritos) {
        return abierto && (inscritos < cupoMaximo);
    }

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
