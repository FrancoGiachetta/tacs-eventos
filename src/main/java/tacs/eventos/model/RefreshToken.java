package tacs.eventos.model;

import java.time.Instant;

public class RefreshToken
{
  private String id;           // UUID
  private String tokenHash;    // SHA-256 del token en claro
  private Instant createdAt;
  private Instant expiresAt;
}
