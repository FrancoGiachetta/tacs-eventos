package tacs.eventos.model;

public enum RolUsuario
{
    ADMIN,
    USUARIO;

    public String asAuthority() {
        return "ROLE_" + this.name();
    }
}
