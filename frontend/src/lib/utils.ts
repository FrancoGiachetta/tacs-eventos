export function formatDate(
    d: Date | string | undefined | null,
    opts?: { withTime: boolean }
): string {
    if (!d) {
        return 'Fecha no disponible'
    }

    try {
        const date = d instanceof Date ? d : new Date(d)

        if (isNaN(date.getTime())) {
            return 'Fecha inválida'
        }

        const day = String(date.getDate()).padStart(2, '0')
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const year = date.getFullYear()
        const hours = String(date.getHours()).padStart(2, '0')
        const minutes = String(date.getMinutes()).padStart(2, '0')

        if (opts?.withTime) {
            return `${day}/${month}/${year} ${hours}:${minutes}hs`
        } else {
            return `${day}/${month}/${year}`
        }
    } catch (error) {
        return 'Fecha inválida'
    }
}
