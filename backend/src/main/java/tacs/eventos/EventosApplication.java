package tacs.eventos;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import tacs.eventos.model.RolUsuario;
import tacs.eventos.model.Usuario;
import tacs.eventos.model.evento.Evento;
import tacs.eventos.repository.usuario.UsuarioRepository;
import tacs.eventos.repository.evento.EventosRepository;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class })
@EnableAsync
public class EventosApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventosApplication.class, args);
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    // seeder: crea usuarios y eventos de ejemplo (solo en desarrollo, no en tests)
    @Bean
    @Profile("!test")
    CommandLineRunner seedData(UsuarioRepository users, EventosRepository eventos, PasswordEncoder pe) {
        return args -> {
            // Crear admin principal si no existe
            Usuario admin = users.findByEmail("admin@events.local").orElseGet(() -> {
                Usuario u = new Usuario("admin@events.local", pe.encode("Admin1234"), Set.of(RolUsuario.ADMIN));
                users.save(u);
                System.out.println("✅ Admin creado: admin@events.local / Admin1234");
                return u;
            });

            // Crear segundo admin
            users.findByEmail("admin2@events.local").orElseGet(() -> {
                Usuario u = new Usuario("admin2@events.local", pe.encode("Admin1234"), Set.of(RolUsuario.ADMIN));
                users.save(u);
                System.out.println("✅ Admin2 creado: admin2@events.local / Admin1234");
                return u;
            });

            // Crear organizador de ejemplo
            Usuario organizador1 = users.findByEmail("organizador@events.local").orElseGet(() -> {
                Usuario u = new Usuario("organizador@events.local", pe.encode("Org1234"),
                        Set.of(RolUsuario.ORGANIZADOR));
                users.save(u);
                System.out.println("✅ Organizador creado: organizador@events.local / Org1234");
                return u;
            });

            // Crear segundo organizador
            Usuario organizador2 = users.findByEmail("organizador2@events.local").orElseGet(() -> {
                Usuario u = new Usuario("organizador2@events.local", pe.encode("Org1234"),
                        Set.of(RolUsuario.ORGANIZADOR));
                users.save(u);
                System.out.println("✅ Organizador2 creado: organizador2@events.local / Org1234");
                return u;
            });

            // Crear usuarios normales de ejemplo
            Usuario usuario1 = users.findByEmail("usuario@events.local").orElseGet(() -> {
                Usuario u = new Usuario("usuario@events.local", pe.encode("User1234"), Set.of(RolUsuario.USUARIO));
                users.save(u);
                System.out.println("✅ Usuario creado: usuario@events.local / User1234");
                return u;
            });

            // Crear eventos de ejemplo si no existen
            if (eventos.findAll().isEmpty()) {
                Evento evento1 = new Evento("Conferencia de Tecnología 2025",
                        "Una conferencia sobre las últimas tendencias en tecnología, IA y desarrollo de software.",
                        LocalDateTime.now().plusDays(30), 120, "Centro de Convenciones Buenos Aires", 100, 2500.0,
                        "Tecnología");
                evento1.setId(UUID.randomUUID().toString());
                evento1.setOrganizador(organizador1);
                eventos.save(evento1);

                Evento evento2 = new Evento("Workshop de React Avanzado",
                        "Aprende técnicas avanzadas de React, hooks personalizados, y optimización de performance.",
                        LocalDateTime.now().plusDays(15), 240, "Coworking Tech Hub", 30, 1500.0, "Desarrollo");
                evento2.setId(UUID.randomUUID().toString());
                evento2.setOrganizador(organizador2);
                eventos.save(evento2);

                Evento evento3 = new Evento("Charla de Ciberseguridad",
                        "Introducción a la ciberseguridad moderna, mejores prácticas y herramientas.",
                        LocalDateTime.now().plusDays(7), 90, "Universidad Tecnológica", 50, 0.0, "Seguridad");
                evento3.setId(UUID.randomUUID().toString());
                evento3.setOrganizador(organizador1);
                eventos.save(evento3);

                Evento evento4 = new Evento("Bootcamp de DevOps",
                        "Curso intensivo de herramientas DevOps: Docker, Kubernetes, CI/CD y monitoreo.",
                        LocalDateTime.now().plusDays(45), 480, "Instituto de Formación Técnica", 25, 3500.0, "DevOps");
                evento4.setId(UUID.randomUUID().toString());
                evento4.setOrganizador(organizador2);
                eventos.save(evento4);

                System.out.println("✅ Eventos de ejemplo creados");
            }
        };
    }
}
