import React, { createContext, useContext, useState, useEffect } from 'react'
import api from '../lib/api'
import { useAuth } from './AuthContext'

export interface Notificacion {
    id: string
    mensaje: string
    tipo: 'info' | 'success' | 'warning' | 'error'
    fechaCreacion: string
    leida: boolean
}

interface NotificationContextType {
    notificaciones: Notificacion[]
    notificacionesNoLeidas: number
    marcarComoLeida: (id: string) => void
    marcarTodasComoLeidas: () => void
    agregarNotificacion: (
        notificacion: Omit<Notificacion, 'id' | 'fechaCreacion' | 'leida'>
    ) => void
}

const NotificationContext = createContext<NotificationContextType | undefined>(
    undefined
)

export function NotificationProvider({
    children,
}: {
    children: React.ReactNode
}) {
    const [notificaciones, setNotificaciones] = useState<Notificacion[]>([])
    const { usuario } = useAuth()

    const agregarNotificacion = (
        notificacion: Omit<Notificacion, 'id' | 'fechaCreacion' | 'leida'>
    ) => {
        const nueva: Notificacion = {
            ...notificacion,
            id: Date.now().toString(),
            fechaCreacion: new Date().toISOString(),
            leida: false,
        }
        setNotificaciones((prev) => [nueva, ...prev])
    }

    // Notificación de bienvenida
    useEffect(() => {
        if (usuario) {
            // Usar setTimeout para asegurar que se muestre después del render
            const timer = setTimeout(() => {
                agregarNotificacion({
                    mensaje: `¡Bienvenido/a ${usuario.email}! 🔔 Las notificaciones están activas. Te avisaremos sobre cambios en tus inscripciones.`,
                    tipo: 'info',
                })
            }, 1000)
            return () => clearTimeout(timer)
        }
    }, [usuario?.email])

    // Estado para rastrear inscripciones previas
    const [inscripcionesPrevias, setInscripcionesPrevias] = useState<any[]>([])

    // Verificar promociones desde waitlist cada 10 segundos
    useEffect(() => {
        if (!usuario) return

        const checkPromociones = async () => {
            try {
                // Obtener mis inscripciones actuales
                const response = await api.get(
                    '/api/v1/usuario/mis-inscripciones'
                )
                const inscripcionesActuales = response.data

                // Si es la primera vez, solo guardar el estado inicial
                if (inscripcionesPrevias.length === 0) {
                    setInscripcionesPrevias(inscripcionesActuales)
                    return
                }

                // Verificar cambios de estado
                inscripcionesActuales.forEach((inscripcionActual: any) => {
                    const inscripcionPrevia = inscripcionesPrevias.find(
                        (prev: any) =>
                            prev.eventoId === inscripcionActual.eventoId
                    )

                    // Si cambió de PENDIENTE a CONFIRMADA
                    if (
                        inscripcionPrevia &&
                        inscripcionPrevia.estado === 'PENDIENTE' &&
                        inscripcionActual.estado === 'CONFIRMADA'
                    ) {
                        // Obtener datos del evento para el mensaje
                        api.get(`/api/v1/evento/${inscripcionActual.eventoId}`)
                            .then((eventoRes) => {
                                agregarNotificacion({
                                    mensaje: `🎉 ¡Excelente! Tu inscripción al evento "${eventoRes.data.titulo}" ha sido CONFIRMADA. Ya no estás en lista de espera.`,
                                    tipo: 'success',
                                })
                            })
                            .catch(() => {
                                agregarNotificacion({
                                    mensaje:
                                        '🎉 ¡Una de tus inscripciones ha sido confirmada! Ya no estás en lista de espera.',
                                    tipo: 'success',
                                })
                            })
                    }

                    // Si se inscribió a un nuevo evento
                    if (!inscripcionPrevia) {
                        api.get(`/api/v1/evento/${inscripcionActual.eventoId}`)
                            .then((eventoRes) => {
                                const mensaje =
                                    inscripcionActual.estado === 'CONFIRMADA'
                                        ? `✅ Te has inscrito correctamente al evento "${eventoRes.data.titulo}".`
                                        : `⏳ Te has inscrito al evento "${eventoRes.data.titulo}". Estás en lista de espera.`

                                agregarNotificacion({
                                    mensaje,
                                    tipo:
                                        inscripcionActual.estado ===
                                        'CONFIRMADA'
                                            ? 'success'
                                            : 'info',
                                })
                            })
                            .catch(() => {
                                agregarNotificacion({
                                    mensaje:
                                        '✅ Te has inscrito a un nuevo evento.',
                                    tipo: 'success',
                                })
                            })
                    }
                })

                // Actualizar el estado previo
                setInscripcionesPrevias(inscripcionesActuales)
            } catch (error) {
                // Silenciosamente ignorar errores de verificación
                console.debug('Error verificando promociones:', error)
            }
        }

        // Verificar inmediatamente y luego cada 10 segundos
        checkPromociones()
        const interval = setInterval(checkPromociones, 10000)

        return () => clearInterval(interval)
    }, [usuario])

    const marcarComoLeida = (id: string) => {
        setNotificaciones((prev) =>
            prev.map((n) => (n.id === id ? { ...n, leida: true } : n))
        )
    }

    const marcarTodasComoLeidas = () => {
        setNotificaciones((prev) => prev.map((n) => ({ ...n, leida: true })))
    }

    const notificacionesNoLeidas = notificaciones.filter((n) => !n.leida).length

    return (
        <NotificationContext.Provider
            value={{
                notificaciones,
                notificacionesNoLeidas,
                marcarComoLeida,
                marcarTodasComoLeidas,
                agregarNotificacion,
            }}
        >
            {children}
        </NotificationContext.Provider>
    )
}

export function useNotifications() {
    const context = useContext(NotificationContext)
    if (context === undefined) {
        throw new Error(
            'useNotifications must be used within a NotificationProvider'
        )
    }
    return context
}
