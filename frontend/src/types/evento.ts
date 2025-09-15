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

export interface EventoFiltros {
    query?: string
    precioPesosMin?: number
    precioPesosMax?: number
    fechaInicioMin?: string
    fechaInicioMax?: string
    categoria?: string
    page?: number
}

export type EstadoEvento = 'abierto' | 'completo' | 'espera'
