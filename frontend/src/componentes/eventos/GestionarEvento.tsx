import { useEffect, useMemo, useRef, useState } from 'react'
import { useParams } from 'react-router-dom'
import api from '../../lib/api'
import { type Evento } from '../../types/evento'
import { type Inscripcion, type ItemWaitlist } from '../../types/inscripciones'
import { formatDate } from '../../lib/utils'

type Tab = 'inscriptos' | 'waitlist'

function Toast({ message, onClose }: { message: string; onClose: () => void }) {
    useEffect(() => {
        const t = setTimeout(onClose, 4000)
        return () => clearTimeout(t)
    }, [onClose])

    return (
        <div className="fixed bottom-4 right-4 z-50 rounded-lg bg-gray-900 text-white shadow-lg px-4 py-3">
            {message}
        </div>
    )
}

export default function GestionarEvento() {
    const { id } = useParams()
    const [evento, setEvento] = useState<Evento | null>(null)
    const [inscriptos, setInscriptos] = useState<Inscripcion[]>([])
    const [waitlist, setWaitlist] = useState<ItemWaitlist[]>([])
    const [tab, setTab] = useState<Tab>('inscriptos')
    const [loading, setLoading] = useState(true)
    const [toggling, setToggling] = useState(false)
    const [error, setError] = useState<string | null>(null)
    const [toast, setToast] = useState<string | null>(null)

    const POLL_MS = 10_000
    const pollRef = useRef<number | null>(null)

    const publicLink = useMemo(() => {
        // Si tu backend te devuelve una URL pública, preferila. Mientras, construimos una razonable:
        return `${window.location.origin}/evento/${id}`
    }, [id])

    const fetchAll = async () => {
        setError(null)
        try {
            const [ev, ins, wl] = await Promise.all([
                api.get(`/api/v1/evento/${id}`),
                api.get(`/api/v1/evento/${id}/inscripcion`),
                api.get(`/api/v1/evento/${id}/waitlist`),
            ])
            setEvento(ev.data)
            setInscriptos(ins.data)
            setWaitlist(wl.data)
        } catch (e: any) {
            setError(e?.message ?? 'Error al cargar los datos')
        } finally {
            setLoading(false)
        }
    }

    useEffect(() => {
        fetchAll()

        const start = () => {
            if (!pollRef.current)
                pollRef.current = window.setInterval(fetchAll, POLL_MS)
        }
        const stop = () => {
            if (pollRef.current) {
                clearInterval(pollRef.current)
                pollRef.current = null
            }
        }

        start()

        const handleVisibility = () => {
            if (document.hidden) stop()
            else {
                fetchAll() // refresh inmediato al volver
                start()
            }
        }

        document.addEventListener('visibilitychange', handleVisibility)

        return () => {
            stop()
            document.removeEventListener('visibilitychange', handleVisibility)
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [id])

    const confirmados = inscriptos.length
    const enEspera = waitlist.length
    const cupo = evento?.cupoMaximo ?? 0

    const handleToggleInscripciones = async () => {
        if (!evento) return
        setToggling(true)
        try {
            // PATCH /api/v1/evento/:id/inscripciones  body: { abierto: boolean }
            const nuevoEstado = !evento.abierto
            await api.patch(`/api/v1/evento/${id}`, {
                abierto: nuevoEstado,
            })
            setEvento({ ...evento, abierto: nuevoEstado })
            setToast(
                nuevoEstado
                    ? 'Inscripciones abiertas'
                    : 'Inscripciones cerradas'
            )
        } catch (e: any) {
            setError(
                e?.message ?? 'No se pudo actualizar el estado de inscripciones'
            )
        } finally {
            setToggling(false)
        }
    }

    const handleQuitar = async (inscripcionId: string) => {
        if (
            !window.confirm(
                '¿Quitar a la persona seleccionada? Esto liberará un lugar.'
            )
        )
            return
        try {
            // DELETE /api/v1/evento/:id/inscripcion/:inscripcionId
            await api.delete(
                `/api/v1/evento/${id}/inscripcion/${inscripcionId}`
            )
            setToast(
                'Inscripción eliminada. El backend promoverá automáticamente si corresponde.'
            )
            await fetchAll()
        } catch (e: any) {
            setError(e?.message ?? 'No se pudo quitar la inscripción')
        }
    }

    const handleCopyLink = async () => {
        try {
            await navigator.clipboard.writeText(publicLink)
            setToast('Link copiado al portapapeles')
        } catch {
            setError('No se pudo copiar el link')
        }
    }

    if (loading) {
        return <div className="p-6">Cargando…</div>
    }

    if (error) {
        return (
            <div className="p-6">
                <p className="text-red-600">Error: {error}</p>
                <button
                    className="mt-3 px-3 py-2 rounded-md bg-gray-800 text-white"
                    onClick={fetchAll}
                >
                    Reintentar
                </button>
            </div>
        )
    }

    if (!evento) {
        return <div className="p-6">No se encontró el evento.</div>
    }

    return (
        <div className="p-6 space-y-6">
            {/* Resumen del evento */}
            <div className="rounded-2xl border bg-white shadow p-5">
                <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
                    <div>
                        <h1 className="text-2xl font-semibold">
                            {evento.titulo}
                        </h1>
                        <p className="text-gray-600">
                            {formatDate(evento.fechaHoraInicio, {
                                withTime: true,
                            })}{' '}
                            · {evento.ubicacion}
                        </p>
                    </div>
                    <div className="flex items-center gap-2">
                        <button
                            onClick={handleToggleInscripciones}
                            disabled={toggling}
                            className={`px-4 py-2 rounded-md text-white font-medium transition-colors ${evento.abierto ? 'bg-rose-600 hover:bg-rose-700' : 'bg-emerald-600 hover:bg-emerald-700'}`}
                            title={
                                evento.abierto
                                    ? 'Cerrar inscripciones para este evento'
                                    : 'Abrir inscripciones para este evento'
                            }
                        >
                            {toggling
                                ? '⏳ Actualizando…'
                                : evento.abierto
                                  ? '🔒 Cerrar inscripciones'
                                  : '🔓 Abrir inscripciones'}
                        </button>
                        <button
                            onClick={handleCopyLink}
                            className="px-3 py-2 rounded-md border bg-white hover:bg-gray-50"
                        >
                            Copiar link público
                        </button>
                    </div>
                </div>

                <div className="mt-4 grid grid-cols-2 sm:grid-cols-4 gap-3">
                    <Stat label="Cupo" value={cupo} />
                    <Stat label="Confirmados" value={confirmados} />
                    <Stat label="En espera" value={enEspera} />
                    <Stat
                        label="Inscripciones"
                        value={evento.abierto ? 'Abiertas' : 'Cerradas'}
                    />
                </div>

                {/* Sección de ayuda */}
                <div className="mt-4 p-3 bg-blue-50 border border-blue-200 rounded-lg">
                    <h4 className="text-sm font-medium text-blue-900 mb-2">
                        💡 Gestión del Evento
                    </h4>
                    <div className="text-xs text-blue-800 space-y-1">
                        <p>
                            • <strong>🔒 Cerrar inscripciones</strong>: Evita
                            nuevas inscripciones al evento
                        </p>
                        <p>
                            • <strong>🔓 Abrir inscripciones</strong>: Permite
                            nuevas inscripciones
                        </p>
                        <p>
                            • <strong>Quitar inscriptos</strong>: Los usuarios
                            en waitlist serán promovidos automáticamente
                        </p>
                    </div>
                </div>
            </div>

            {/* Pestañas */}
            <div className="rounded-2xl border bg-white shadow">
                <div className="border-b flex">
                    <button
                        onClick={() => setTab('inscriptos')}
                        className={`px-4 py-3 font-medium ${tab === 'inscriptos' ? 'border-b-2 border-gray-900' : 'text-gray-600'}`}
                    >
                        Inscriptos ({confirmados})
                    </button>
                    <button
                        onClick={() => setTab('waitlist')}
                        className={`px-4 py-3 font-medium ${tab === 'waitlist' ? 'border-b-2 border-gray-900' : 'text-gray-600'}`}
                    >
                        Waitlist ({enEspera})
                    </button>
                </div>

                {tab === 'inscriptos' ? (
                    <InscriptosTable
                        inscriptos={inscriptos}
                        onQuitar={handleQuitar}
                    />
                ) : (
                    <WaitlistTable waitlist={waitlist} />
                )}
            </div>

            {toast && <Toast message={toast} onClose={() => setToast(null)} />}
        </div>
    )
}

function Stat({ label, value }: { label: string; value: string | number }) {
    return (
        <div className="rounded-xl border bg-gray-50 p-4">
            <div className="text-xs uppercase tracking-wide text-gray-500">
                {label}
            </div>
            <div className="text-xl font-semibold">{value}</div>
        </div>
    )
}

function InscriptosTable({
    inscriptos,
    onQuitar,
}: {
    inscriptos: Inscripcion[]
    onQuitar: (inscripcionId: string) => void
}) {
    return (
        <div className="overflow-x-auto p-4">
            <table className="min-w-full text-sm">
                <thead>
                    <tr className="bg-gray-100 text-gray-700">
                        <th className="text-left px-3 py-2">Email</th>
                        <th className="text-left px-3 py-2">
                            Fecha de inscripción
                        </th>
                        <th className="text-left px-3 py-2">Acciones</th>
                    </tr>
                </thead>
                <tbody>
                    {inscriptos.map((i) => (
                        <tr key={i.id} className="odd:bg-white even:bg-gray-50">
                            <td className="px-3 py-2">{i.email ?? '—'}</td>
                            <td className="px-3 py-2">
                                {i.fechaInscripcion
                                    ? formatDate(new Date(i.fechaInscripcion), {
                                          withTime: true,
                                      })
                                    : '—'}
                            </td>
                            <td className="px-3 py-2">
                                <button
                                    onClick={() => onQuitar(i.id)}
                                    className="px-2 py-1 rounded-md bg-rose-600 text-white hover:bg-rose-700"
                                >
                                    Quitar
                                </button>
                            </td>
                        </tr>
                    ))}
                    {inscriptos.length === 0 && (
                        <tr>
                            <td
                                colSpan={3}
                                className="px-3 py-6 text-center text-gray-500"
                            >
                                No hay inscriptos.
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    )
}

function WaitlistTable({ waitlist }: { waitlist: ItemWaitlist[] }) {
    return (
        <div className="overflow-x-auto p-4">
            <table className="min-w-full text-sm">
                <thead>
                    <tr className="bg-gray-100 text-gray-700">
                        <th className="text-left px-3 py-2">Posición</th>
                        <th className="text-left px-3 py-2">Nombre</th>
                        <th className="text-left px-3 py-2">Fecha de alta</th>
                    </tr>
                </thead>
                <tbody>
                    {waitlist.map((w, idx) => (
                        <tr
                            key={w.usuario.id ?? `${w.usuario.email}-${idx}`}
                            className="odd:bg-white even:bg-gray-50"
                        >
                            <td className="px-3 py-2">{idx + 1}</td>
                            <td className="px-3 py-2">
                                {w.fechaIngreso
                                    ? formatDate(new Date(w.fechaIngreso), {
                                          withTime: true,
                                      })
                                    : '—'}
                            </td>
                        </tr>
                    ))}
                    {waitlist.length === 0 && (
                        <tr>
                            <td
                                colSpan={3}
                                className="px-3 py-6 text-center text-gray-500"
                            >
                                No hay personas en espera.
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    )
}
