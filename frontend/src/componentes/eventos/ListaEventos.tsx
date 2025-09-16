import { useState, useEffect } from 'react'
import { useAuth } from '../../hooks/useAuth'
import api from '../../lib/api'
import EventCard from '../EventCard'
import EventFilters from '../EventFilters'
import { Evento, EventoFiltros } from '../../types/evento'

export default function ListaEventos() {
    const [eventos, setEventos] = useState<Evento[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState<string | null>(null)
    const { token } = useAuth()
    const [filtros, setFiltros] = useState<EventoFiltros>({})

    useEffect(() => {
        const fetchEventos = async () => {
            try {
                setLoading(true)
                setError(null)

                const queryParams = new URLSearchParams()
                if (filtros.query)
                    queryParams.append('palabrasClave', filtros.query)
                if (filtros.precioPesosMin !== undefined)
                    queryParams.append(
                        'precioPesosMin',
                        filtros.precioPesosMin.toString()
                    )
                if (filtros.precioPesosMax !== undefined)
                    queryParams.append(
                        'precioPesosMax',
                        filtros.precioPesosMax.toString()
                    )
                if (filtros.fechaInicioMin)
                    queryParams.append('fechaInicioMin', filtros.fechaInicioMin)
                if (filtros.fechaInicioMax)
                    queryParams.append('fechaInicioMax', filtros.fechaInicioMax)
                if (filtros.categoria)
                    queryParams.append('categoria', filtros.categoria)
                if (filtros.page)
                    queryParams.append('page', filtros.page.toString())

                const response = await api.get(
                    `/api/v1/evento?${queryParams.toString()}`,
                    {
                        headers: { Authorization: `Bearer ${token}` },
                    }
                )
                setEventos(response.data)
            } catch (err) {
                setError('Error al cargar los eventos')
                console.error('Error fetching eventos:', err)
            } finally {
                setLoading(false)
            }
        }
        fetchEventos()
    }, [filtros, token])

    const handleFiltroChange = (nuevosFiltros: EventoFiltros) => {
        setFiltros(nuevosFiltros)
    }

    if (loading)
        return <div className="text-center py-4">Cargando eventos...</div>
    if (error)
        return <div className="text-red-600 text-center py-4">{error}</div>

    return (
        <div className="container mx-auto px-4 py-8">
            <EventFilters
                onFilterChange={handleFiltroChange}
                onReset={() => setFiltros({})}
            />
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 mt-6">
                {eventos.length > 0 ? (
                    eventos.map((evento) => (
                        <EventCard
                            key={evento.id}
                            evento={evento}
                            onVerDetalle={(eventoId) => {
                                location.href = `/eventos/${eventoId}`
                            }}
                        />
                    ))
                ) : (
                    <div className="col-span-full text-center text-gray-500">
                        No se encontraron eventos
                    </div>
                )}
            </div>
        </div>
    )
}
