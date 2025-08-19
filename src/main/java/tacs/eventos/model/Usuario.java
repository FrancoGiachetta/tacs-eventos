package tacs.eventos.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Usuario {
    private String id;
    private String email; // almacenadr normalizado (lowercase)
    private String passwordHash; // BCrypt
    private Set<RolUsuario> roles = new HashSet<>();
    private Instant fechaCreacion = Instant.now();

    // getters y setters
    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public Set<RolUsuario> getRoles() {
        return roles;
    }

    public Usuario(String email, String passwordHash, Set<RolUsuario> roles) {
        this.id = java.util.UUID.randomUUID().toString(); // Generar un ID único todo: que la base de datos lo genere /
                                                          // que se genere en funcion de los ids existentes
        this.email = email.toLowerCase(); // Normalizar a minúsculas
        this.passwordHash = passwordHash;
        this.roles = roles != null ? roles : new HashSet<>();
    }

}
