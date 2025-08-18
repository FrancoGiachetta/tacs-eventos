package tacs.eventos;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;
import tacs.eventos.repository.RepositorioUsuarios;

import java.time.Instant;
import java.util.Set;

import static java.util.Set.of;

@SpringBootApplication
public class EventosApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventosApplication.class, args);
    }

    @Bean
        PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(12); }

    // seeder: crea admin si no existe
    @Bean
    CommandLineRunner seedAdmin(RepositorioUsuarios users, PasswordEncoder pe) {
        return args -> {
            users.findByEmailIgnoreCase("admin@events.local").orElseGet(() -> {
                Usuario u = new Usuario("admin@events.local", pe.encode("Admin1234!cambialo"), Set.of(RolUsuario.ADMIN));
                return users.save(u);
            });
        };
    }

}
