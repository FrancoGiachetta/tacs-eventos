export interface Inscripcion {
    eventoId: string
    estado: 'CONFITMADA' | 'WAITLIST' | 'CANCELADA'
    createdAt: string
}

export interface ItemWaitlist {
    usuario: {
        id: string
        email: string
    }
    fechaIngreso: string
}