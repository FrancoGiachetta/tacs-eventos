package tacs.eventos.model;

import java.time.Instant;

public class Session {
    private String token; // UUID aleatorio en texto
    private String userId;
    private Instant expiresAt;
    private boolean active = true;

    public Session() {
    }

    public Session(String token, String userId, Instant expiresAt) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
    }

    public String getToken() {
        return token;
    }

    public void deactivate() {
        this.active = false;
    }

    public boolean isActive() {
        return active;
    }

    public String getUserId() {
        return userId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }
}
