import { type InputCrearEvento } from './lib/schemas'

/* Este tipo tiene todos los campos del formulario, sumado a los campos que no se muestran en el formulario porque los completa el back end en forma automática */
export type Evento = InputCrearEvento & {
    id: string
    abierto: boolean
}

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

export interface Estadisticas {}

export interface Usuario {
    mail: string
    role: 'usuario' | 'admin'
}

export interface ErrorDelServidor {
    errores: Error[]
}

export interface Error {
    campo: string
    mensaje: string
}

export function esError(obj: any): obj is Error {
    return 'campo' in obj && 'mensaje' in obj
}

export function esErrorDelServidor(obj: any): obj is ErrorDelServidor {
    return (
        'errores' in obj &&
        Array.isArray(obj.errores) &&
        obj.errores.every(esError)
    )
}
