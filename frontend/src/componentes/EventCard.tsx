import type { Evento } from '../types/evento'
import { formatearFecha } from '../utils'

interface EventCardProps {
    evento: Evento
    onVerDetalle: (eventoId: string) => void
}

export default function EventCard({ evento, onVerDetalle }: EventCardProps) {
    const getEstadoChip = () => {
        if (!evento.abierto)
            return { text: 'Completo', color: 'bg-red-100 text-red-800' }
        if (evento.cupoMaximo === 0)
            return {
                text: 'Lista de espera',
                color: 'bg-yellow-100 text-yellow-800',
            }
        return { text: 'Abierto', color: 'bg-green-100 text-green-800' }
    }

    const chip = getEstadoChip()

    return (
        <div className="bg-white rounded-lg shadow-md overflow-hidden">
            <div className="p-4">
                <h3 className="text-lg font-semibold mb-2">{evento.titulo}</h3>

                <div className="flex items-center gap-2 text-sm text-gray-600 mb-2">
                    <span>{formatearFecha(evento.fechaHoraInicio)}</span>
                    <span>•</span>
                    <span>{evento.ubicacion}</span>
                </div>

                <p className="text-gray-600 mb-4 line-clamp-2">
                    {evento.descripcion}
                </p>

                <div className="flex justify-between items-center mb-4">
                    <div className="flex gap-2">
                        <span
                            className={`px-2 py-1 rounded-full text-sm ${chip.color}`}
                        >
                            {chip.text}
                        </span>
                        {evento.categoria && (
                            <span className="px-2 py-1 bg-gray-100 text-gray-800 rounded-full text-sm">
                                {evento.categoria}
                            </span>
                        )}
                    </div>
                    <span className="font-semibold">
                        {evento.precio === 0 ? 'Gratis' : `$${evento.precio}`}
                    </span>
                </div>

                <div className="flex justify-between items-center">
                    <span className="text-sm text-gray-600">
                        {evento.cupoMaximo} lugares disponibles
                    </span>
                    <button
                        onClick={() => onVerDetalle(evento.id)}
                        className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                    >
                        Ver detalle
                    </button>
                </div>
            </div>
        </div>
    )
}
