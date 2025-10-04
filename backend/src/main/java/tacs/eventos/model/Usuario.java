package tacs.eventos.model;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "usuarios")
@Getter

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String email; // almacenadr normalizado (lowercase)
    private String passwordHash; // BCrypt
    private Set<RolUsuario> roles = new HashSet<>();
    private Instant fechaCreacion = Instant.now();

    public Usuario(String email, String passwordHash, Set<RolUsuario> roles) {
        this.id = java.util.UUID.randomUUID().toString(); // Generar un ID único todo: que la base de datos lo genere /
        // que se genere en funcion de los ids existentes
        this.email = email.toLowerCase(); // Normalizar a minúsculas
        this.passwordHash = passwordHash;
        this.roles = roles != null ? roles : new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Usuario usuario && this.id.equals(usuario.getId());
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    public boolean tieneRol(RolUsuario rol) {
        return this.roles.contains(rol);
    }

    public void setRoles(Set<RolUsuario> roles) {
        this.roles = roles != null ? roles : new HashSet<>();
    }
}
