import React, { useState } from 'react'
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
                    buttonStyle={buttonStyle}
                    buttonDisabledStyle={buttonDisabledStyle}
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
    buttonStyle: React.CSSProperties
    buttonDisabledStyle: React.CSSProperties
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
    buttonStyle,
    buttonDisabledStyle,
}) => {
    return (
        <div>
            <h2 className="text-2xl font-bold mb-6">Panel de Estadísticas</h2>
            <div>
                <button
                    onClick={fetchInscripciones}
                    disabled={loadingInscripciones}
                    style={
                        loadingInscripciones ? buttonDisabledStyle : buttonStyle
                    }
                >
                    {loadingInscripciones
                        ? 'Cargando...'
                        : 'Obtener total de inscripciones'}
                </button>
                {errorInscripciones && (
                    <p style={{ color: 'red' }}>{errorInscripciones}</p>
                )}
                {totalInscripciones !== null && (
                    <p>
                        <strong>Total de inscripciones:</strong>{' '}
                        {totalInscripciones}
                    </p>
                )}
            </div>
            <div>
                <button
                    onClick={fetchEventos}
                    disabled={loadingEventos}
                    style={loadingEventos ? buttonDisabledStyle : buttonStyle}
                >
                    {loadingEventos
                        ? 'Cargando...'
                        : 'Obtener total de eventos'}
                </button>
                {errorEventos && <p style={{ color: 'red' }}>{errorEventos}</p>}
                {totalEventos !== null && (
                    <p>
                        <strong>Total de eventos:</strong> {totalEventos}
                    </p>
                )}
            </div>
            <div>
                <input
                    type="text"
                    placeholder="ID de evento"
                    value={idEvento}
                    onChange={(e) => setIdEvento(e.target.value)}
                    style={{
                        padding: '8px',
                        fontSize: '16px',
                        borderRadius: '4px',
                        border: '1px solid #ccc',
                        marginRight: '10px',
                    }}
                />
                <button
                    onClick={fetchTasaConversionWL}
                    disabled={loadingTasa}
                    style={loadingTasa ? buttonDisabledStyle : buttonStyle}
                >
                    {loadingTasa
                        ? 'Cargando...'
                        : 'Obtener tasa de conversión Wait List'}
                </button>
                {errorTasa && <p style={{ color: 'red' }}>{errorTasa}</p>}
                {tasaConversionWL !== null && (
                    <p>
                        <strong>Tasa de conversión Wait List:</strong>{' '}
                        {tasaConversionWL}
                    </p>
                )}
            </div>
        </div>
    )
}

export default Admin
