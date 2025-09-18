import { type InputCrearEvento } from '../lib/schemas'

/* Este tipo tiene todos los campos del formulario, sumado a los campos que no se muestran en el formulario porque los completa el back end en forma automática */
export type Evento = InputCrearEvento & {
    id: string
    abierto: boolean
}

export interface EventoFiltros {
    query?: string
    precioPesosMin?: number
    precioPesosMax?: number
    fechaInicioMin?: string
    fechaInicioMax?: string
    categoria?: string
    page?: number
}

// export type EstadoEvento = 'abierto' | 'completo' | 'espera' // Ahora está modelado como el booleano 'abierto', pero lo vamos a cambiar
