import React, { useState, useEffect } from 'react'
import GestionUsuarios from './GestionUsuarios'
import api from '../../lib/api'

function Admin() {
    const [vistaActual, setVistaActual] = useState<'estadisticas' | 'usuarios'>(
        'estadisticas'
    )
    const [totalInscripciones, setTotalInscripciones] = useState<number | null>(
        null
    )
    const [totalEventos, setTotalEventos] = useState<number | null>(null)
    const [tasaConversionWL, setTasaConversionWL] = useState<number | null>(
        null
    )
    const [loadingInscripciones, setLoadingInscripciones] = useState(false)
    const [loadingEventos, setLoadingEventos] = useState(false)
    const [loadingTasa, setLoadingTasa] = useState(false)
    const [errorInscripciones, setErrorInscripciones] = useState<string | null>(
        null
    )
    const [errorEventos, setErrorEventos] = useState<string | null>(null)
    const [errorTasa, setErrorTasa] = useState<string | null>(null)
    const [idEvento, setIdEvento] = useState<string>('')

    const buttonStyle: React.CSSProperties = {
        backgroundColor: '#1976d2',
        color: 'white',
        border: 'none',
        borderRadius: '5px',
        padding: '10px 20px',
        margin: '10px 0',
        cursor: 'pointer',
        fontSize: '16px',
        fontWeight: 'bold',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
        transition: 'background 0.2s',
    }

    const buttonDisabledStyle: React.CSSProperties = {
        ...buttonStyle,
        backgroundColor: '#90caf9',
        cursor: 'not-allowed',
    }

    const fetchInscripciones = async () => {
        setLoadingInscripciones(true)
        setErrorInscripciones(null)
        try {
            const res = await api.get(
                '/api/v1/estadisticas/inscripciones/total'
            )
            setTotalInscripciones(res.data.total ?? res.data)
        } catch (err: any) {
            setErrorInscripciones(err.message)
        } finally {
            setLoadingInscripciones(false)
        }
    }

    const fetchEventos = async () => {
        setLoadingEventos(true)
        setErrorEventos(null)
        try {
            const res = await api.get('/api/v1/estadisticas/eventos/total')
            setTotalEventos(res.data.total ?? res.data)
        } catch (err: any) {
            setErrorEventos(err.message)
        } finally {
            setLoadingEventos(false)
        }
    }

    const fetchTasaConversionWL = async () => {
        if (!idEvento) {
            setErrorTasa('Ingrese un ID de evento')
            return
        }
        setLoadingTasa(true)
        setErrorTasa(null)
        setTasaConversionWL(null)
        try {
            const res = await api.get(
                `/api/v1/estadisticas/eventos/${idEvento}/tasa-conversionwl`
            )
            setTasaConversionWL(res.data.tasa ?? res.data)
        } catch (err: any) {
            setErrorTasa(err.message)
        } finally {
            setLoadingTasa(false)
        }
    }

    // Auto-fetch resumen cuando se muestra la vista de estadísticas
    useEffect(() => {
        if (vistaActual === 'estadisticas') {
            // arrancamos las métricas principales
            fetchInscripciones()
            fetchEventos()
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [vistaActual])

    return (
        <div className="container mx-auto px-4 py-8">
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4 mb-6">
                <h1 className="text-3xl font-bold text-blue-900 mb-2">
                    Panel de Administración
                </h1>
                <p className="text-blue-700">
                    <strong>👋 Bienvenido, Admin!</strong> Desde aquí puedes
                    gestionar usuarios y ver estadísticas del sistema.
                </p>
            </div>

            {/* Navegación por pestañas */}
            <div className="flex border-b border-gray-200 mb-6">
                <button
                    onClick={() => setVistaActual('estadisticas')}
                    className={`px-6 py-3 font-medium text-sm border-b-2 transition-colors ${
                        vistaActual === 'estadisticas'
                            ? 'border-blue-500 text-blue-600'
                            : 'border-transparent text-gray-500 hover:text-gray-700'
                    }`}
                >
                    📊 Estadísticas
                </button>
                <button
                    onClick={() => setVistaActual('usuarios')}
                    className={`px-6 py-3 font-medium text-sm border-b-2 transition-colors ${
                        vistaActual === 'usuarios'
                            ? 'border-blue-500 text-blue-600'
                            : 'border-transparent text-gray-500 hover:text-gray-700'
                    }`}
                >
                    👥 Gestión de Usuarios
                </button>
                {/* Pestaña de Gestión de Eventos eliminada (redundante) */}
            </div>

            {/* Contenido según la vista actual */}
            {vistaActual === 'estadisticas' ? (
                <EstadisticasPanel
                    totalInscripciones={totalInscripciones}
                    setTotalInscripciones={setTotalInscripciones}
                    totalEventos={totalEventos}
                    setTotalEventos={setTotalEventos}
                    tasaConversionWL={tasaConversionWL}
                    setTasaConversionWL={setTasaConversionWL}
                    loadingInscripciones={loadingInscripciones}
                    setLoadingInscripciones={setLoadingInscripciones}
                    loadingEventos={loadingEventos}
                    setLoadingEventos={setLoadingEventos}
                    loadingTasa={loadingTasa}
                    setLoadingTasa={setLoadingTasa}
                    errorInscripciones={errorInscripciones}
                    setErrorInscripciones={setErrorInscripciones}
                    errorEventos={errorEventos}
                    setErrorEventos={setErrorEventos}
                    errorTasa={errorTasa}
                    setErrorTasa={setErrorTasa}
                    idEvento={idEvento}
                    setIdEvento={setIdEvento}
                    fetchInscripciones={fetchInscripciones}
                    fetchEventos={fetchEventos}
                    fetchTasaConversionWL={fetchTasaConversionWL}
                />
            ) : (
                <GestionUsuarios />
            )}
        </div>
    )
}

// Componente separado para las estadísticas
interface EstadisticasPanelProps {
    totalInscripciones: number | null
    setTotalInscripciones: (value: number | null) => void
    totalEventos: number | null
    setTotalEventos: (value: number | null) => void
    tasaConversionWL: number | null
    setTasaConversionWL: (value: number | null) => void
    loadingInscripciones: boolean
    setLoadingInscripciones: (value: boolean) => void
    loadingEventos: boolean
    setLoadingEventos: (value: boolean) => void
    loadingTasa: boolean
    setLoadingTasa: (value: boolean) => void
    errorInscripciones: string | null
    setErrorInscripciones: (value: string | null) => void
    errorEventos: string | null
    setErrorEventos: (value: string | null) => void
    errorTasa: string | null
    setErrorTasa: (value: string | null) => void
    idEvento: string
    setIdEvento: (value: string) => void
    fetchInscripciones: () => Promise<void>
    fetchEventos: () => Promise<void>
    fetchTasaConversionWL: () => Promise<void>
}

const EstadisticasPanel: React.FC<EstadisticasPanelProps> = ({
    totalInscripciones,
    totalEventos,
    tasaConversionWL,
    loadingInscripciones,
    loadingEventos,
    loadingTasa,
    errorInscripciones,
    errorEventos,
    errorTasa,
    idEvento,
    setIdEvento,
    fetchInscripciones,
    fetchEventos,
    fetchTasaConversionWL,
}) => {
    return (
        <div className="space-y-8">
            <h2 className="text-2xl font-bold text-gray-900 mb-6">
                📊 Panel de Estadísticas
            </h2>

            {/* Cards de estadísticas principales */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-8">
                {/* Card Inscripciones */}
                <div className="bg-gradient-to-br from-blue-50 to-blue-100 border border-blue-200 rounded-xl p-6 shadow-sm">
                    <div className="flex items-center justify-between mb-4">
                        <div className="flex items-center">
                            <div className="p-2 bg-blue-500 rounded-lg mr-3">
                                <span className="text-white text-xl">👥</span>
                            </div>
                            <h3 className="text-lg font-semibold text-blue-900">
                                Total de Inscripciones
                            </h3>
                        </div>
                    </div>

                    {totalInscripciones !== null && (
                        <div className="mb-4">
                            <p className="text-3xl font-bold text-blue-600">
                                {totalInscripciones.toLocaleString()}
                            </p>
                            <p className="text-sm text-blue-500">
                                inscripciones en el sistema
                            </p>
                        </div>
                    )}

                    <button
                        onClick={fetchInscripciones}
                        disabled={loadingInscripciones}
                        className={`w-full px-4 py-2 rounded-lg font-medium transition-colors ${
                            loadingInscripciones
                                ? 'bg-blue-300 text-blue-100 cursor-not-allowed'
                                : 'bg-blue-500 hover:bg-blue-600 text-white'
                        }`}
                    >
                        {loadingInscripciones ? (
                            <span className="flex items-center justify-center">
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                                Cargando...
                            </span>
                        ) : (
                            '🔄 Actualizar Inscripciones'
                        )}
                    </button>

                    {errorInscripciones && (
                        <div className="mt-3 p-3 bg-red-100 border border-red-300 rounded-lg">
                            <p className="text-red-700 text-sm">
                                ❌ {errorInscripciones}
                            </p>
                        </div>
                    )}
                </div>

                {/* Card Eventos */}
                <div className="bg-gradient-to-br from-green-50 to-green-100 border border-green-200 rounded-xl p-6 shadow-sm">
                    <div className="flex items-center justify-between mb-4">
                        <div className="flex items-center">
                            <div className="p-2 bg-green-500 rounded-lg mr-3">
                                <span className="text-white text-xl">📅</span>
                            </div>
                            <h3 className="text-lg font-semibold text-green-900">
                                Total de Eventos
                            </h3>
                        </div>
                    </div>

                    {totalEventos !== null && (
                        <div className="mb-4">
                            <p className="text-3xl font-bold text-green-600">
                                {totalEventos.toLocaleString()}
                            </p>
                            <p className="text-sm text-green-500">
                                eventos creados
                            </p>
                        </div>
                    )}

                    <button
                        onClick={fetchEventos}
                        disabled={loadingEventos}
                        className={`w-full px-4 py-2 rounded-lg font-medium transition-colors ${
                            loadingEventos
                                ? 'bg-green-300 text-green-100 cursor-not-allowed'
                                : 'bg-green-500 hover:bg-green-600 text-white'
                        }`}
                    >
                        {loadingEventos ? (
                            <span className="flex items-center justify-center">
                                <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                                Cargando...
                            </span>
                        ) : (
                            '🔄 Actualizar Eventos'
                        )}
                    </button>

                    {errorEventos && (
                        <div className="mt-3 p-3 bg-red-100 border border-red-300 rounded-lg">
                            <p className="text-red-700 text-sm">
                                ❌ {errorEventos}
                            </p>
                        </div>
                    )}
                </div>
            </div>

            {/* Card Tasa de Conversión */}
            <div className="bg-gradient-to-br from-purple-50 to-purple-100 border border-purple-200 rounded-xl p-6 shadow-sm">
                <div className="flex items-center mb-4">
                    <div className="p-2 bg-purple-500 rounded-lg mr-3">
                        <span className="text-white text-xl">📈</span>
                    </div>
                    <h3 className="text-lg font-semibold text-purple-900">
                        Tasa de Conversión Waitlist
                    </h3>
                </div>

                <div className="mb-4">
                    <label className="block text-sm font-medium text-purple-700 mb-2">
                        ID del Evento
                    </label>
                    <input
                        type="text"
                        placeholder="Ingrese el ID del evento"
                        value={idEvento}
                        onChange={(e) => setIdEvento(e.target.value)}
                        className="w-full px-4 py-2 border border-purple-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-purple-500 focus:border-transparent"
                    />
                </div>

                {tasaConversionWL !== null && (
                    <div className="mb-4">
                        <p className="text-3xl font-bold text-purple-600">
                            {tasaConversionWL}%
                        </p>
                        <p className="text-sm text-purple-500">
                            de conversión desde waitlist
                        </p>
                    </div>
                )}

                <button
                    onClick={fetchTasaConversionWL}
                    disabled={loadingTasa || !idEvento.trim()}
                    className={`w-full px-4 py-2 rounded-lg font-medium transition-colors ${
                        loadingTasa || !idEvento.trim()
                            ? 'bg-purple-300 text-purple-100 cursor-not-allowed'
                            : 'bg-purple-500 hover:bg-purple-600 text-white'
                    }`}
                >
                    {loadingTasa ? (
                        <span className="flex items-center justify-center">
                            <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white mr-2"></div>
                            Calculando...
                        </span>
                    ) : (
                        '📊 Calcular Tasa de Conversión'
                    )}
                </button>

                {errorTasa && (
                    <div className="mt-3 p-3 bg-red-100 border border-red-300 rounded-lg">
                        <p className="text-red-700 text-sm">❌ {errorTasa}</p>
                    </div>
                )}

                <div className="mt-4 p-3 bg-purple-50 rounded-lg">
                    <p className="text-xs text-purple-600">
                        💡 <strong>¿Qué es la tasa de conversión?</strong>
                        <br />
                        Porcentaje de personas que pasan de la waitlist a estar
                        confirmadas en el evento.
                    </p>
                </div>
            </div>

            {/* Resumen visual cuando hay datos */}
            {(totalInscripciones !== null || totalEventos !== null) && (
                <div className="bg-gray-50 border border-gray-200 rounded-xl p-6">
                    <h4 className="text-lg font-semibold text-gray-900 mb-4">
                        📋 Resumen del Sistema
                    </h4>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
                        {totalInscripciones !== null && (
                            <div className="text-center">
                                <p className="text-2xl font-bold text-blue-600">
                                    {totalInscripciones}
                                </p>
                                <p className="text-sm text-gray-600">
                                    Inscripciones
                                </p>
                            </div>
                        )}
                        {totalEventos !== null && (
                            <div className="text-center">
                                <p className="text-2xl font-bold text-green-600">
                                    {totalEventos}
                                </p>
                                <p className="text-sm text-gray-600">Eventos</p>
                            </div>
                        )}
                        {totalInscripciones !== null &&
                            totalEventos !== null &&
                            totalEventos > 0 && (
                                <div className="text-center">
                                    <p className="text-2xl font-bold text-orange-600">
                                        {(
                                            totalInscripciones / totalEventos
                                        ).toFixed(1)}
                                    </p>
                                    <p className="text-sm text-gray-600">
                                        Inscripciones promedio por evento
                                    </p>
                                </div>
                            )}
                    </div>
                </div>
            )}
        </div>
    )
}

export default Admin
