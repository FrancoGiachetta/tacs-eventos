import { useState, useRef, useEffect } from 'react'
import { useNotifications } from '../contexts/NotificationContext'

export default function NotificationDropdown() {
    const { notificaciones, notificacionesNoLeidas, marcarComoLeida, marcarTodasComoLeidas, agregarNotificacion } = useNotifications()
    const [isOpen, setIsOpen] = useState(false)
    const dropdownRef = useRef<HTMLDivElement>(null)

    // Cerrar dropdown al hacer click fuera
    useEffect(() => {
        function handleClickOutside(event: MouseEvent) {
            if (dropdownRef.current && !dropdownRef.current.contains(event.target as Node)) {
                setIsOpen(false)
            }
        }

        document.addEventListener('mousedown', handleClickOutside)
        return () => {
            document.removeEventListener('mousedown', handleClickOutside)
        }
    }, [])

    const formatearFecha = (fecha: string) => {
        const ahora = new Date()
        const fechaNotif = new Date(fecha)
        const diffMinutos = Math.floor((ahora.getTime() - fechaNotif.getTime()) / (1000 * 60))
        
        if (diffMinutos < 1) return 'Hace un momento'
        if (diffMinutos < 60) return `Hace ${diffMinutos} min`
        
        const diffHoras = Math.floor(diffMinutos / 60)
        if (diffHoras < 24) return `Hace ${diffHoras}h`
        
        const diffDias = Math.floor(diffHoras / 24)
        return `Hace ${diffDias} días`
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

    const handleNotificacionClick = (id: string) => {
        marcarComoLeida(id)
    }

    return (
        <div className="relative" ref={dropdownRef}>
            <button
                onClick={() => setIsOpen(!isOpen)}
                className="relative p-2 text-gray-600 hover:text-gray-900 focus:outline-none focus:ring-2 focus:ring-blue-500 rounded-lg"
            >
                <i className="fas fa-bell text-xl"></i>
                {notificacionesNoLeidas > 0 && (
                    <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center">
                        {notificacionesNoLeidas > 9 ? '9+' : notificacionesNoLeidas}
                    </span>
                )}
            </button>

            {isOpen && (
                <div className="absolute right-0 mt-2 w-80 bg-white rounded-lg shadow-lg border border-gray-200 z-50">
                    <div className="p-4 border-b border-gray-200">
                        <div className="flex items-center justify-between">
                            <h3 className="text-lg font-semibold">Notificaciones</h3>
                            {notificacionesNoLeidas > 0 && (
                                <button
                                    onClick={marcarTodasComoLeidas}
                                    className="text-sm text-blue-600 hover:text-blue-800"
                                >
                                    Marcar todas como leídas
                                </button>
                            )}
                        </div>
                        {/* Botón de prueba temporal */}
                        <button
                            onClick={() => agregarNotificacion({
                                mensaje: '🧪 Prueba: Las notificaciones están funcionando correctamente!',
                                tipo: 'success'
                            })}
                            className="mt-2 text-xs bg-green-100 text-green-800 px-2 py-1 rounded"
                        >
                            Probar notificación
                        </button>
                    </div>

                    <div className="max-h-96 overflow-y-auto">
                        {notificaciones.length === 0 ? (
                            <div className="p-4 text-center text-gray-500">
                                No tienes notificaciones
                            </div>
                        ) : (
                            notificaciones.slice(0, 10).map((notif) => (
                                <div
                                    key={notif.id}
                                    onClick={() => handleNotificacionClick(notif.id)}
                                    className={`p-4 border-b border-gray-100 hover:bg-gray-50 cursor-pointer transition-colors ${
                                        !notif.leida ? 'bg-blue-50' : ''
                                    }`}
                                >
                                    <div className="flex items-start space-x-3">
                                        <span className="text-lg mt-0.5">
                                            {getIconoTipo(notif.tipo)}
                                        </span>
                                        <div className="flex-1 min-w-0">
                                            <p className={`text-sm ${!notif.leida ? 'font-medium' : 'font-normal'} text-gray-900`}>
                                                {notif.mensaje}
                                            </p>
                                            <p className="text-xs text-gray-500 mt-1">
                                                {formatearFecha(notif.fechaCreacion)}
                                            </p>
                                        </div>
                                        {!notif.leida && (
                                            <div className="w-2 h-2 bg-blue-500 rounded-full mt-2"></div>
                                        )}
                                    </div>
                                </div>
                            ))
                        )}
                    </div>

                    {notificaciones.length > 10 && (
                        <div className="p-4 border-t border-gray-200 text-center">
                            <span className="text-sm text-gray-500">
                                Mostrando las 10 notificaciones más recientes
                            </span>
                        </div>
                    )}
                </div>
            )}
        </div>
    )
}