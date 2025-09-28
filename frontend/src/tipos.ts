// Re-exportamos todos los tipos desde sus módulos específicos
export type { Evento, UsuarioEvento, EventoFiltros } from './types/evento'
export type { CategoriaEvento } from './types/categorias'
export type {
    Inscripcion,
    InscripcionResponse,
    ItemInscripcion,
    ItemWaitlist,
} from './types/inscripciones'
export type { InputCrearEvento } from './lib/schemas'
