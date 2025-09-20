package tacs.eventos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tacs.eventos.auth.SessionAuthFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final SessionAuthFilter sessionFilter;

    public SecurityConfig(SessionAuthFilter sessionFilter) {
        this.sessionFilter = sessionFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/swagger").permitAll()
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(sessionFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
