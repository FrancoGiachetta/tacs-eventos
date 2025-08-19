package tacs.eventos.dto;

import java.time.Instant;

public record SessionResponse(String token, Instant expiresAt) {
}
