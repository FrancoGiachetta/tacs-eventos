export function formatearFecha(fecha: string): string {
  const f = new Date(fecha);
  return new Intl.DateTimeFormat('es-AR', {
    day: '2-digit',
    month: 'short',
    hour: '2-digit',
    minute: '2-digit'
  }).format(f);
}
