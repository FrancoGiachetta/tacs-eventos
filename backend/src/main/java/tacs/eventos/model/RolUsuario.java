package tacs.eventos.model;

public enum RolUsuario {
    ADMIN, // Administrador del sistema - ve estadísticas
    ORGANIZADOR, // Puede crear y gestionar eventos
    USUARIO; // Participante normal - se inscribe a eventos

    public String asAuthority() {
        return "ROLE_" + this.name();
    }
}
