// Tipos de roles de usuario según el enunciado
export enum RolUsuario {
    ADMIN = 'ADMIN',
    ORGANIZADOR = 'ORGANIZADOR',
    USUARIO = 'USUARIO',
}

// Tipo para información del usuario autenticado
export interface UsuarioAutenticado {
    id: string
    email: string
    roles: RolUsuario[]
}

// Helper functions para verificar roles
export const esAdmin = (usuario: UsuarioAutenticado | null): boolean => {
    return usuario?.roles.includes(RolUsuario.ADMIN) ?? false
}

export const esOrganizador = (usuario: UsuarioAutenticado | null): boolean => {
    return usuario?.roles.includes(RolUsuario.ORGANIZADOR) ?? false
}

export const esUsuarioNormal = (
    usuario: UsuarioAutenticado | null
): boolean => {
    return usuario?.roles.includes(RolUsuario.USUARIO) ?? false
}

// Verificar si el usuario tiene al menos uno de los roles especificados
export const tieneAlgunRol = (
    usuario: UsuarioAutenticado | null,
    roles: RolUsuario[]
): boolean => {
    if (!usuario) return false
    return roles.some((rol) => usuario.roles.includes(rol))
}

// Obtener el rol "principal" del usuario (para mostrar en UI)
export const getRolPrincipal = (
    usuario: UsuarioAutenticado | null
): RolUsuario | null => {
    if (!usuario || usuario.roles.length === 0) return null

    // Prioridad: Admin > Organizador > Usuario
    if (usuario.roles.includes(RolUsuario.ADMIN)) return RolUsuario.ADMIN
    if (usuario.roles.includes(RolUsuario.ORGANIZADOR))
        return RolUsuario.ORGANIZADOR
    if (usuario.roles.includes(RolUsuario.USUARIO)) return RolUsuario.USUARIO

    return usuario.roles[0] // fallback al primer rol
}
