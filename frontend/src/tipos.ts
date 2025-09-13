export interface Evento {
    id: string
    titulo: string
    descripcion: string
    fechaHoraInicio: string
    duracionMinutos: number
    ubicacion: string
    cupoMaximo: number
    precio: number
    categoria?: string | null
    abierto: boolean
}

export interface Inscripcion {
    eventoId: string
    estado: 'CONFITMADA' | 'WAITLIST' | 'CANCELADA'
    createdAt: string
}

export interface ItemWaitlist {
    usuario: {
        id: string,
        email: string
    },
    fechaIngreso: string
}

export interface Estadisticas {}

export interface Usuario {
    mail: string
    role: 'usuario' | 'admin'
}
