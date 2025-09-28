package tacs.eventos.dto;

import tacs.eventos.model.RolUsuario;

import java.time.Instant;

public record UsuarioDto(String id, String email, RolUsuario rol, Instant fechaCreacion) {
}