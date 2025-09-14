import {type InputCrearEvento} from './lib/schemas'

/* Este tipo tiene todos los campos del formulario, sumado a los campos que no se muestran en el formulario porque los completa el back end en forma automática */
export type Evento = InputCrearEvento & {
    id: string
    abierto: boolean
}

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

export interface Estadisticas {}

export interface Usuario {
    mail: string
    role: 'usuario' | 'admin'
}
