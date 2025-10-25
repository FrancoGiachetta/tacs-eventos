import React, { useState, useEffect } from 'react'
import api from '../../lib/api'
import { Evento } from '../../types/evento'
import { toast } from '../../lib/simpleToast'
import { formatDate } from '../../lib/utils'
import { useAuth } from '../../contexts/AuthContext'
import { esAdmin } from '../../types/usuario'

const GestionEventos: React.FC = () => {
    const [eventos, setEventos] = useState<Evento[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')
    const [eventoEditando, setEventoEditando] = useState<Evento | null>(null)
    const [mostrarModal, setMostrarModal] = useState(false)
    const [eventoAEliminar, setEventoAEliminar] = useState<Evento | null>(null)
    const { usuario } = useAuth()

    useEffect(() => {
        cargarEventos()
    }, [])

    const cargarEventos = async () => {
        try {
            setLoading(true)
            const token = localStorage.getItem('authToken')

            // Si es admin, obtener todos los eventos, sino solo mis eventos
            const endpoint = esAdmin(usuario)
                ? '/api/v1/evento'
                : '/api/v1/usuario/mis-eventos'

            const response = await api.get(endpoint, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
            setEventos(response.data)
            setError('')
        } catch (err) {
            setError('Error al cargar eventos')
            console.error('Error:', err)
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
                // Actualizar evento existente
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
                // Crear nuevo evento
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

    const getEstadoColor = (abierto: boolean) => {
        return abierto
            ? 'bg-green-100 text-green-800'
            : 'bg-red-100 text-red-800'
    }

    const getEstadoTexto = (abierto: boolean) => {
        return abierto ? 'Abierto' : 'Cerrado'
    }

    if (loading) {
        return (
            <div className="flex justify-center items-center h-64">
                <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
            </div>
        )
    }

    if (error) {
        return (
            <div className="bg-red-50 border border-red-200 rounded-lg p-4">
                <p className="text-red-800">{error}</p>
                <button
                    onClick={cargarEventos}
                    className="mt-2 px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
                >
                    Reintentar
                </button>
            </div>
        )
    }

    return (
        <div className="space-y-6">
            <div className="flex justify-between items-center">
                <h2 className="text-2xl font-bold text-gray-900">
                    {esAdmin(usuario)
                        ? 'Gestión de Todos los Eventos'
                        : 'Mis Eventos'}
                </h2>
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
                    <div className="overflow-x-auto">
                        <table className="min-w-full divide-y divide-gray-200">
                            <thead className="bg-gray-50">
                                <tr>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Evento
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Fecha/Hora
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Ubicación
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Cupo
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Precio
                                    </th>
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Estado
                                    </th>
                                    {esAdmin(usuario) && (
                                        <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                            Organizador
                                        </th>
                                    )}
                                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                        Acciones
                                    </th>
                                </tr>
                            </thead>
                            <tbody className="bg-white divide-y divide-gray-200">
                                {eventos.map((evento) => (
                                    <tr
                                        key={evento.id}
                                        className="hover:bg-gray-50"
                                    >
                                        <td className="px-6 py-4">
                                            <div>
                                                <div className="text-sm font-medium text-gray-900">
                                                    {evento.titulo}
                                                </div>
                                                <div className="text-sm text-gray-500">
                                                    {evento.categoria}
                                                </div>
                                                <div className="text-xs text-gray-400">
                                                    ID: {evento.id}
                                                </div>
                                            </div>
                                        </td>
                                        <td className="px-6 py-4 text-sm text-gray-900">
                                            {formatDate(
                                                evento.fechaHoraInicio,
                                                { withTime: true }
                                            )}
                                        </td>
                                        <td className="px-6 py-4 text-sm text-gray-900">
                                            {evento.ubicacion}
                                        </td>
                                        <td className="px-6 py-4 text-sm text-gray-900">
                                            {evento.cupoMaximo}
                                        </td>
                                        <td className="px-6 py-4 text-sm text-gray-900">
                                            ${evento.precio.toLocaleString()}
                                        </td>
                                        <td className="px-6 py-4">
                                            <span
                                                className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getEstadoColor(evento.abierto)}`}
                                            >
                                                {getEstadoTexto(evento.abierto)}
                                            </span>
                                        </td>
                                        {esAdmin(usuario) && (
                                            <td className="px-6 py-4 text-sm text-gray-900">
                                                <div>
                                                    <div>
                                                        {
                                                            evento.organizador
                                                                .email
                                                        }
                                                    </div>
                                                    <div className="text-xs text-gray-400">
                                                        ID:{' '}
                                                        {evento.organizador.id}
                                                    </div>
                                                </div>
                                            </td>
                                        )}
                                        <td className="px-6 py-4 text-sm font-medium space-x-2">
                                            <button
                                                onClick={() =>
                                                    handleEditarEvento(evento)
                                                }
                                                className="text-blue-600 hover:text-blue-900 transition-colors"
                                            >
                                                ✏️ Editar
                                            </button>
                                            <button
                                                onClick={() =>
                                                    handleEliminarEvento(evento)
                                                }
                                                className="text-red-600 hover:text-red-900 transition-colors"
                                            >
                                                🗑️ Eliminar
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                </div>
            )}

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
        </div>
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

        // Convertir la fecha de string a formato ISO para el backend
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

export default GestionEventos
