package tacs.eventos.model;

import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "usuarios")
public class Usuario {
    @Getter
    private String id;
    @Getter
    private String email; // almacenadr normalizado (lowercase)
    @Getter
    private String passwordHash; // BCrypt
    @Getter
    private Set<RolUsuario> roles = new HashSet<>();
    @Getter
    private Instant fechaCreacion = Instant.now();

    public Usuario(String email, String passwordHash, Set<RolUsuario> roles) {
        this.id = java.util.UUID.randomUUID().toString();
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

}
