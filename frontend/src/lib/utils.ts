export function formatDate(d: Date, opts?: { withTime: boolean }) {
    const day = String(d.getDate()).padStart(2, '0')
    const month = String(d.getMonth() + 1).padStart(2, '0')
    const year = d.getFullYear()
    const hours = String(d.getHours()).padStart(2, '0')
    const minutes = String(d.getMinutes()).padStart(2, '0')

    if (opts?.withTime) {
        return `${day}/${month}/${year} ${hours}:${minutes}hs`
    } else {
        return `${day}/${month}/${year}`
    }
}