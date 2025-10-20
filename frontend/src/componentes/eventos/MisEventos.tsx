import { useEffect, useState } from 'react'
import api from '../../lib/api'
import type { Evento } from '../../types/evento'
import type { Inscripcion, ItemWaitlist } from '../../types/inscripciones'
import { formatDate } from '../../lib/utils'

import { toast } from '../../lib/simpleToast'
import { useAuth } from '../../contexts/AuthContext'
import { esAdmin } from '../../types/usuario'

export default function MisEventos() {
    const [eventos, setEventos] = useState<Evento[]>([])
    const [loading, setLoading] = useState(true)
    const [eventoEditando, setEventoEditando] = useState<Evento | null>(null)
    const [mostrarModal, setMostrarModal] = useState(false)
    const { usuario } = useAuth()

    useEffect(() => {
        cargarEventos()
    }, [])

    const cargarEventos = async () => {
        try {
            setLoading(true)
            const response = await api.get('/api/v1/usuario/mis-eventos')
            setEventos(response.data)
        } catch (error) {
            console.error('Error al cargar eventos:', error)
            toast.error('Error al cargar eventos')
        } finally {
            setLoading(false)
        }
    }

    const handleEditarEvento = (evento: Evento) => {
        setEventoEditando(evento)
        setMostrarModal(true)
    }

    const handleEliminarEvento = async (evento: Evento) => {
        if (
            window.confirm(
                `¿Está seguro que desea eliminar el evento "${evento.titulo}"?`
            )
        ) {
            try {
                const token = localStorage.getItem('authToken')
                await api.delete(`/api/v1/evento/${evento.id}`, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                })
                toast.success('Evento eliminado exitosamente')
                cargarEventos()
            } catch (err) {
                toast.error('Error al eliminar evento')
                console.error('Error:', err)
            }
        }
    }

    const handleGuardarEvento = async (eventoData: any) => {
        try {
            const token = localStorage.getItem('authToken')

            if (eventoEditando) {
                await api.put(
                    `/api/v1/evento/${eventoEditando.id}`,
                    eventoData,
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                        },
                    }
                )
                toast.success('Evento actualizado exitosamente')
            } else {
                await api.post('/api/v1/evento', eventoData, {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                })
                toast.success('Evento creado exitosamente')
            }

            setMostrarModal(false)
            setEventoEditando(null)
            cargarEventos()
        } catch (err) {
            toast.error('Error al guardar evento')
            console.error('Error:', err)
        }
    }

    if (loading) {
        return (
            <div className="flex justify-center items-center h-64">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
            </div>
        )
    }

    return (
        <>
            <div className="mt-5">
                <div className="flex justify-between items-center mb-7">
                    <h1 className="text-3xl">
                        {esAdmin(usuario) ? 'Todos los eventos' : 'Mis eventos'}
                    </h1>
                    <button
                        onClick={() => {
                            setEventoEditando(null)
                            setMostrarModal(true)
                        }}
                        className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
                    >
                        + Crear Evento
                    </button>
                </div>

                {eventos.length === 0 ? (
                    <div className="text-center py-12">
                        <div className="text-gray-400 text-6xl mb-4">📅</div>
                        <h3 className="text-xl font-medium text-gray-900 mb-2">
                            No hay eventos
                        </h3>
                        <p className="text-gray-500">
                            Comienza creando tu primer evento
                        </p>
                    </div>
                ) : (
                    <div className="bg-white shadow-sm rounded-lg overflow-hidden">
                        <table className="table-auto border-collapse min-w-full text-sm text-left">
                            <thead>
                                <tr className="table-row font-bold bg-gray-50 text-gray-600">
                                    <th className="px-4 py-3">Nombre</th>
                                    <th className="px-4 py-3">Fecha</th>
                                    <th className="px-4 py-3">Ubicación</th>
                                    <th className="px-4 py-3">Estado</th>
                                    <th className="px-4 py-3">Inscriptos</th>
                                    <th className="px-4 py-3">En Waitlist</th>
                                    <th className="px-4 py-3">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {eventos.map((evento: Evento) => (
                                    <EventoRow
                                        key={evento.id}
                                        evento={evento}
                                        onEditar={handleEditarEvento}
                                        onEliminar={handleEliminarEvento}
                                    />
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>

            {/* Modal para crear/editar evento */}
            {mostrarModal && (
                <ModalEvento
                    evento={eventoEditando}
                    onGuardar={handleGuardarEvento}
                    onCerrar={() => {
                        setMostrarModal(false)
                        setEventoEditando(null)
                    }}
                />
            )}
        </>
    )
}

interface EventoRowProps {
    evento: Evento
    onEditar: (evento: Evento) => void
    onEliminar: (evento: Evento) => void
}

function EventoRow({ evento, onEditar, onEliminar }: EventoRowProps) {
    const [inscripciones, setInscripciones] = useState<Inscripcion[]>([])
    const [waitlist, setWaitlist] = useState<ItemWaitlist[]>([])
    const { usuario } = useAuth()

    useEffect(() => {
        const fetchData = async () => {
            try {
                const [inscripcionesRes, waitlistRes] = await Promise.all([
                    api.get(`/api/v1/evento/${evento.id}/inscripcion`),
                    api.get(`/api/v1/evento/${evento.id}/waitlist`),
                ])
                setInscripciones(inscripcionesRes.data)
                setWaitlist(waitlistRes.data)
            } catch (error) {
                console.error('Error fetching event data:', error)
            }
        }

        fetchData()
    }, [evento.id])

    return (
        <tr className="table-row even:bg-gray-50 odd:bg-white hover:bg-gray-100">
            <td className="px-4 py-3">
                <div>
                    <div className="font-medium text-gray-900">
                        {evento.titulo}
                    </div>
                    <div className="text-sm text-gray-500">
                        {evento.categoria}
                    </div>
                    <div className="text-xs text-gray-400">ID: {evento.id}</div>
                </div>
            </td>
            <td className="px-4 py-3">
                {formatDate(evento.fechaHoraInicio, { withTime: true })}
            </td>
            <td className="px-4 py-3">{evento.ubicacion}</td>
            <td className="px-4 py-3">
                <span
                    className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${
                        evento.abierto
                            ? 'bg-green-100 text-green-800'
                            : 'bg-red-100 text-red-800'
                    }`}
                >
                    {evento.abierto ? 'Abierto' : 'Cerrado'}
                </span>
            </td>
            <td className="px-4 py-3">
                <span className="font-medium">{inscripciones.length}</span>
                <span className="text-gray-500">/{evento.cupoMaximo}</span>
            </td>
            <td className="px-4 py-3">{waitlist.length}</td>
            <td className="px-4 py-3">
                <div className="flex items-center gap-2">
                    <button
                        onClick={() => onEditar(evento)}
                        className="px-2 py-1 text-blue-600 hover:text-blue-800 text-sm font-medium transition-colors"
                        title="Editar evento"
                    >
                        ✏️ Editar
                    </button>
                    <button
                        onClick={() => onEliminar(evento)}
                        className="px-2 py-1 text-red-600 hover:text-red-800 text-sm font-medium transition-colors"
                        title="Eliminar evento"
                    >
                        🗑️ Eliminar
                    </button>
                    <a
                        href={`/organizador/eventos/${evento.id}`}
                        className="px-3 py-1 text-green-600 hover:text-green-800 text-sm font-medium transition-colors border border-green-600 hover:border-green-800 rounded"
                        title="Gestionar inscripciones"
                    >
                        Gestionar
                    </a>
                    <a
                        href={`/eventos/${evento.id}`}
                        className="px-3 py-1 text-blue-600 hover:text-blue-800 text-sm font-medium transition-colors border border-blue-600 hover:border-blue-800 rounded"
                        title="Ver detalle del evento"
                    >
                        Ver detalle
                    </a>
                </div>
            </td>
        </tr>
    )
}

// Componente Modal para crear/editar eventos
interface ModalEventoProps {
    evento: Evento | null
    onGuardar: (evento: any) => void
    onCerrar: () => void
}

const ModalEvento: React.FC<ModalEventoProps> = ({
    evento,
    onGuardar,
    onCerrar,
}) => {
    const [formData, setFormData] = useState({
        titulo: evento?.titulo || '',
        descripcion: evento?.descripcion || '',
        fechaHoraInicio: evento?.fechaHoraInicio
            ? new Date(evento.fechaHoraInicio).toISOString().slice(0, 16)
            : '',
        duracionMinutos: evento?.duracionMinutos || 60,
        ubicacion: evento?.ubicacion || '',
        cupoMaximo: evento?.cupoMaximo || 50,
        precio: evento?.precio || 0,
        categoria: evento?.categoria || 'General',
    })

    const categorias = [
        'Tecnología',
        'Desarrollo',
        'Seguridad',
        'DevOps',
        'IA',
        'Diseño',
        'Marketing',
        'Negocios',
        'Educación',
        'General',
    ]

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault()

        const eventoData = {
            ...formData,
            fechaHoraInicio: new Date(formData.fechaHoraInicio).toISOString(),
        }

        onGuardar(eventoData)
    }

    const handleChange = (
        e: React.ChangeEvent<
            HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
        >
    ) => {
        const { name, value, type } = e.target
        setFormData((prev) => ({
            ...prev,
            [name]: type === 'number' ? parseFloat(value) || 0 : value,
        }))
    }

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
            <div className="bg-white rounded-lg p-6 max-w-2xl w-full mx-4 max-h-screen overflow-y-auto">
                <div className="flex justify-between items-center mb-6">
                    <h3 className="text-xl font-semibold">
                        {evento ? 'Editar Evento' : 'Crear Nuevo Evento'}
                    </h3>
                    <button
                        onClick={onCerrar}
                        className="text-gray-400 hover:text-gray-600"
                    >
                        ✕
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Título *
                        </label>
                        <input
                            type="text"
                            name="titulo"
                            value={formData.titulo}
                            onChange={handleChange}
                            required
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Descripción *
                        </label>
                        <textarea
                            name="descripcion"
                            value={formData.descripcion}
                            onChange={handleChange}
                            required
                            rows={3}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Fecha y Hora *
                            </label>
                            <input
                                type="datetime-local"
                                name="fechaHoraInicio"
                                value={formData.fechaHoraInicio}
                                onChange={handleChange}
                                required
                                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Duración (minutos) *
                            </label>
                            <input
                                type="number"
                                name="duracionMinutos"
                                value={formData.duracionMinutos}
                                onChange={handleChange}
                                required
                                min="1"
                                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                        </div>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">
                            Ubicación *
                        </label>
                        <input
                            type="text"
                            name="ubicacion"
                            value={formData.ubicacion}
                            onChange={handleChange}
                            required
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Cupo Máximo *
                            </label>
                            <input
                                type="number"
                                name="cupoMaximo"
                                value={formData.cupoMaximo}
                                onChange={handleChange}
                                required
                                min="1"
                                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Precio ($)
                            </label>
                            <input
                                type="number"
                                name="precio"
                                value={formData.precio}
                                onChange={handleChange}
                                min="0"
                                step="0.01"
                                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                        </div>

                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">
                                Categoría *
                            </label>
                            <select
                                name="categoria"
                                value={formData.categoria}
                                onChange={handleChange}
                                required
                                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                            >
                                {categorias.map((cat) => (
                                    <option key={cat} value={cat}>
                                        {cat}
                                    </option>
                                ))}
                            </select>
                        </div>
                    </div>

                    <div className="flex justify-end space-x-3 pt-4">
                        <button
                            type="button"
                            onClick={onCerrar}
                            className="px-4 py-2 text-gray-700 bg-gray-200 rounded-md hover:bg-gray-300 transition-colors"
                        >
                            Cancelar
                        </button>
                        <button
                            type="submit"
                            className="px-4 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 transition-colors"
                        >
                            {evento ? 'Actualizar' : 'Crear'} Evento
                        </button>
                    </div>
                </form>
            </div>
        </div>
    )
}
