package tacs.eventos.model;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class Evento {
    @Getter
    private String id;
    @Getter
    private String titulo;
    @Getter
    private String descripcion;
    @Getter
    private LocalDateTime fechaHoraInicio;
    @Getter
    private int duracionMinutos;
    @Getter
    private String ubicacion;
    @Getter
    private int cupoMaximo;
    @Getter
    private int inscritos;
    @Getter
    private double precio;
    @Getter
    private String categoria;
    @Getter
    private boolean abierto;

    @Getter
    private List<String> participantes;
    @Getter
    private Queue<String> waitlist;

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
        this.inscritos = 0;
        this.participantes = new LinkedList<>();
        this.waitlist = new LinkedList<>();
    }

    public boolean agregarParticipante(String usuarioId) {
        if (!abierto)
            return false;
        if (inscritos < cupoMaximo) {
            participantes.add(usuarioId);
            inscritos++;
            return true;
        } else {
            waitlist.add(usuarioId);
            return false;
        }
    }

    public boolean cancelarParticipante(String usuarioId) {
        boolean removed = participantes.remove(usuarioId);
        if (removed) {
            inscritos--;
            if (!waitlist.isEmpty()) {
                String next = waitlist.poll();
                participantes.add(next);
                inscritos++;
            }
            return true;
        } else {
            return waitlist.remove(usuarioId);
        }
    }

    public void cerrarEvento() {
        this.abierto = false;
    }

}
