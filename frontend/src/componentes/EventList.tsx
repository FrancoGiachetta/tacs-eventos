import type { Evento } from '../types/evento'
import EventCard from './EventCard'

interface EventListProps {
    eventos: Evento[]
    isLoading: boolean
    error?: string
    onRetry: () => void
    onVerDetalle: (eventoId: string) => void
}

export default function EventList({
    eventos,
    isLoading,
    error,
    onRetry,
    onVerDetalle,
}: EventListProps) {
    if (isLoading) {
        return (
            <div className="flex justify-center items-center min-h-[200px]">
                <div className="animate-spin rounded-full h-12 w-12 border-4 border-blue-500 border-t-transparent"></div>
            </div>
        )
    }

    if (error) {
        return (
            <div className="text-center py-8">
                <p className="text-red-600 mb-4">{error}</p>
                <button
                    onClick={onRetry}
                    className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                >
                    Reintentar
                </button>
            </div>
        )
    }

    if (eventos.length === 0) {
        return (
            <div className="text-center py-8 text-gray-600">
                No encontramos eventos
            </div>
        )
    }

    return (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            {eventos.map((evento) => (
                <EventCard
                    key={evento.id}
                    evento={evento}
                    onVerDetalle={onVerDetalle}
                />
            ))}
        </div>
    )
}
