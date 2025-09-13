package tacs.eventos.dto;

import tacs.eventos.model.RolUsuario;

import java.util.Set;

public record UsuarioResponse(String id, String email, Set<RolUsuario> roles) {
}