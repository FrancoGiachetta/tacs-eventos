package tacs.eventos.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Session {
    private String token; // UUID aleatorio en texto
    private String userId;
    private Instant expiresAt;
    private boolean active;
}
