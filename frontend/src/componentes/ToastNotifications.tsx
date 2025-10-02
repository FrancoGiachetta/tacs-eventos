import { useEffect, useState } from 'react'
import { useNotifications } from '../contexts/NotificationContext'

export default function ToastNotifications() {
    const { notificaciones } = useNotifications()
    const [toastsVisibles, setToastsVisibles] = useState<string[]>([])

    useEffect(() => {
        if (notificaciones.length > 0) {
            const ultimaNotificacion = notificaciones[0]
            
            // Solo mostrar toast si es nueva (no leída y no está ya visible)
            if (!ultimaNotificacion.leida && !toastsVisibles.includes(ultimaNotificacion.id)) {
                setToastsVisibles(prev => [ultimaNotificacion.id, ...prev])
                
                // Auto-remover después de 5 segundos
                setTimeout(() => {
                    setToastsVisibles(prev => prev.filter(id => id !== ultimaNotificacion.id))
                }, 5000)
            }
        }
    }, [notificaciones, toastsVisibles])

    const removerToast = (id: string) => {
        setToastsVisibles(prev => prev.filter(toastId => toastId !== id))
    }

    const getIconoTipo = (tipo: string) => {
        switch (tipo) {
            case 'success':
                return '✅'
            case 'warning':
                return '⚠️'
            case 'error':
                return '❌'
            default:
                return 'ℹ️'
        }
    }

    const getColorTipo = (tipo: string) => {
        switch (tipo) {
            case 'success':
                return 'border-green-400 bg-green-50 text-green-800'
            case 'warning':
                return 'border-yellow-400 bg-yellow-50 text-yellow-800'
            case 'error':
                return 'border-red-400 bg-red-50 text-red-800'
            default:
                return 'border-blue-400 bg-blue-50 text-blue-800'
        }
    }

    if (toastsVisibles.length === 0) return null

    return (
        <div className="fixed top-20 right-4 z-50 space-y-2">
            {toastsVisibles.map(toastId => {
                const notificacion = notificaciones.find(n => n.id === toastId)
                if (!notificacion) return null

                return (
                    <div
                        key={toastId}
                        className={`max-w-sm p-4 rounded-lg border-l-4 shadow-lg transform transition-all duration-300 ease-in-out animate-slide-in-right ${getColorTipo(notificacion.tipo)}`}
                    >
                        <div className="flex items-start space-x-3">
                            <span className="text-lg flex-shrink-0">
                                {getIconoTipo(notificacion.tipo)}
                            </span>
                            <div className="flex-1 min-w-0">
                                <p className="text-sm font-medium">
                                    {notificacion.mensaje}
                                </p>
                            </div>
                            <button
                                onClick={() => removerToast(toastId)}
                                className="flex-shrink-0 text-lg hover:opacity-70"
                            >
                                ×
                            </button>
                        </div>
                    </div>
                )
            })}
        </div>
    )
}