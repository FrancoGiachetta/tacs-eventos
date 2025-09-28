export function formatearFecha(fecha: string | undefined | null): string {
    if (!fecha) {
        return 'Fecha no disponible'
    }

    try {
        const f = new Date(fecha)
        if (isNaN(f.getTime())) {
            return 'Fecha inválida'
        }
        return new Intl.DateTimeFormat('es-AR', {
            day: '2-digit',
            month: 'short',
            hour: '2-digit',
            minute: '2-digit',
        }).format(f)
    } catch (error) {
        return 'Fecha inválida'
    }
}
