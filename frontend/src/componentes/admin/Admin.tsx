import React, { useState } from 'react'

function Admin() {
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
            const res = await fetch(
                'http://localhost:8080/api/v1/estadisticas/inscripciones/total'
            )
            if (!res.ok) throw new Error('Error al obtener inscripciones')
            const data = await res.json()
            setTotalInscripciones(data.total ?? data)
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
            const res = await fetch(
                'http://localhost:8080/api/v1/estadisticas/eventos/total'
            )
            if (!res.ok) throw new Error('Error al obtener eventos')
            const data = await res.json()
            setTotalEventos(data.total ?? data)
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
            const res = await fetch(
                `http://localhost:8080/api/v1/estadisticas/eventos/${idEvento}/tasa-conversionwl`
            )
            if (!res.ok) throw new Error('Error al obtener tasa de conversión')
            const data = await res.json()
            setTasaConversionWL(data.tasa ?? data)
        } catch (err: any) {
            setErrorTasa(err.message)
        } finally {
            setLoadingTasa(false)
        }
    }

    return (
        <div>
            <h1>Panel de Estadisticas</h1>
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
