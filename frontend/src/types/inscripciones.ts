export interface Inscripcion {
    id: string
    eventoId: string
    estado: 'CONFITMADA' | 'WAITLIST' | 'CANCELADA'
    email?: string
    fechaInscripcion?: string
}

export interface ItemWaitlist {
    usuario: {
        id: string
        email: string
    }
    fechaIngreso: string
}
