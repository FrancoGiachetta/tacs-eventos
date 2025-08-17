package tacs.eventos.model;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

public class Evento {

    private String id;
    private String titulo;
    private String descripcion;
    private LocalDateTime fechaHoraInicio;
    private int duracionMinutos;
    private String ubicacion;
    private int cupoMaximo;
    private int inscritos;
    private double precio;
    private String categoria;
    private boolean abierto;

    private List<String> participantes;
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

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public int getInscritos() {
        return inscritos;
    }

    public double getPrecio() {
        return precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public boolean isAbierto() {
        return abierto;
    }

    public List<String> getParticipantes() {
        return participantes;
    }

    public Queue<String> getWaitlist() {
        return waitlist;
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
