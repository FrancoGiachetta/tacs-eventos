import { useEffect, useRef, useState } from 'react'
import type { Evento } from '../../types/evento'

export default function EventoActionMenu(props: {
    evento: Evento
    className?: string
}) {
    const [open, setOpen] = useState(false)
    const [position, setPosition] = useState({ top: 0, left: 0 })
    const menuRef = useRef<HTMLDivElement | null>(null)
    const buttonRef = useRef<HTMLButtonElement | null>(null)
    const evento = props.evento

    useEffect(() => {
        const handler = (e: MouseEvent) => {
            if (
                menuRef.current &&
                !menuRef.current.contains(e.target as Node) &&
                buttonRef.current &&
                !buttonRef.current.contains(e.target as Node)
            ) {
                setOpen(false)
            }
        }
        document.addEventListener('mousedown', handler)
        return () => document.removeEventListener('mousedown', handler)
    }, [])

    const handleToggle = () => {
        if (!open && buttonRef.current) {
            const rect = buttonRef.current.getBoundingClientRect()
            const viewportHeight = window.innerHeight
            const viewportWidth = window.innerWidth
            const menuWidth = 192 // w-48 = 12rem = 192px
            const menuHeight = 90 // Altura real del menú (80px minHeight + padding)
            
            // Calcular posición vertical
            const spaceBelow = viewportHeight - rect.bottom
            const shouldPositionAbove = spaceBelow < menuHeight + 10
            
            let top = shouldPositionAbove 
                ? rect.top + window.scrollY - menuHeight - 5  // Posicionar arriba
                : rect.bottom + window.scrollY + 5           // Posicionar abajo
            
            // Calcular posición horizontal (alineado a la derecha del botón)
            let left = rect.right + window.scrollX - menuWidth
            
            // Ajustar si se sale por la derecha
            if (left + menuWidth > window.scrollX + viewportWidth - 10) {
                left = window.scrollX + viewportWidth - menuWidth - 10
            }
            
            // Ajustar si se sale por la izquierda
            if (left < window.scrollX + 10) {
                left = window.scrollX + 10
            }
            
            setPosition({ top, left })
        }
        setOpen(!open)
    }

    return (
        <>
            {/* Button */}
            <button
                ref={buttonRef}
                onClick={handleToggle}
                className={`${props.className || ''} p-2 text-gray-500 hover:text-gray-700 hover:bg-gray-100 rounded-lg transition-colors`}
                title="Más opciones"
            >
                <i className="fa-solid fa-ellipsis"></i>
            </button>

            {/* Dropdown usando posicionamiento fijo */}
            {open && (
                <div
                    ref={menuRef}
                    className="fixed z-50 w-48 border border-gray-200 shadow-lg bg-white rounded-lg transition-all duration-200 ease-out"
                    style={{
                        top: `${position.top}px`,
                        left: `${position.left}px`,
                        minHeight: '80px'
                    }}
                >
                    <ul className="text-gray-700 py-1">
                        <li>
                            <a
                                className="block w-full text-left px-4 py-3 hover:bg-gray-100 transition-colors duration-150"
                                href={`/eventos/${evento.id}`}
                            >
                                📄 Ver detalle
                            </a>
                        </li>
                        <li>
                            <a
                                className="block w-full text-left px-4 py-3 hover:bg-gray-100 transition-colors duration-150"
                                href={`/organizador/eventos/${evento.id}`}
                            >
                                ⚙️ Gestionar inscriptos
                            </a>
                        </li>
                    </ul>
                </div>
            )}
        </>
    )
}
