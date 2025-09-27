export interface Inscripcion {
    id: string
    eventoId: string
    estado: 'CONFITMADA' | 'WAITLIST' | 'CANCELADA'
    email?: string
    fechaInscripcion?: string
}

export interface InscripcionResponse {
    eventoId: string
    estado: 'CONFIRMADA' | 'PENDIENTE' | 'CANCELADA'
    email?: string
    fechaInscripcion?: string
    id?: string
}

export interface ItemInscripcion {
    usuario: {
        id: string
        email: string
    }
    fechaInscripcion: string
}

export interface ItemWaitlist {
    usuario: {
        id: string
        email: string
    }
    fechaIngreso: string
}
