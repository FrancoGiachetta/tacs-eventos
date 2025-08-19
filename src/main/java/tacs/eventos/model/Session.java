package tacs.eventos.model;

import java.time.Instant;

public class Session {
    private String token; // UUID aleatorio en texto
    private String userId;
    private Instant expiresAt;
    private boolean active;

    public Session() {
    }

    public Session(String token, String userId, Instant expiresAt, boolean active) {
        this.token = token;
        this.userId = userId;
        this.expiresAt = expiresAt;
        this.active = active;
    }

    public String getToken() {
        return token;
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
