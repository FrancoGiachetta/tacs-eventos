import type { EventoFiltros } from '../types/evento'
interface EventFiltersProps {
    onFilterChange: (filtros: EventoFiltros) => void
    onReset: () => void
}
export default function EventFilters({
    onFilterChange,
    onReset,
}: EventFiltersProps): import('react/jsx-runtime').JSX.Element
export {}
