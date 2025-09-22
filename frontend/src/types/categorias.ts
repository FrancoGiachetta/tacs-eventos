// Enum de categorías de eventos basado en las que se usan en el backend
export const CATEGORIAS_EVENTO = [
    'Deporte',
    'Moda',
    'Educacion',
    'Tecnologia',
    'Musica',
    'Gastronomia',
    'Arte',
    'Negocios',
    'Salud',
    'Entretenimiento',
] as const

export type CategoriaEvento = (typeof CATEGORIAS_EVENTO)[number]

// Helper function para verificar si una string es una categoría válida
export const esCategoriaValida = (
    categoria: string
): categoria is CategoriaEvento => {
    return CATEGORIAS_EVENTO.includes(categoria as CategoriaEvento)
}
