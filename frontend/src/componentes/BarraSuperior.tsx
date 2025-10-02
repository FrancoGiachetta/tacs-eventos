import { Link, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import { useAuth } from '../contexts/AuthContext'
import {
    esAdmin,
    esOrganizador,
    getRolPrincipal,
    RolUsuario,
} from '../types/usuario'
import NotificationDropdown from './NotificationDropdown'

export default function BarraSuperior() {
    const [open, setOpen] = useState(false)
    const navigate = useNavigate()
    const { usuario, logout } = useAuth()

    const handleLogout = async () => {
        await logout()
        navigate('/login')
    }

    if (!usuario) {
        return null // No mostrar barra si no está autenticado
    }

    const rolPrincipal = getRolPrincipal(usuario)
    const esSysAdmin = esAdmin(usuario)
    const esOrg = esOrganizador(usuario)

    return (
        <header className="sticky top-0 z-40 bg-blue-400 text-white">
            <div className="mx-auto max-w-6xl px-4">
                <div className="flex h-14 items-center justify-between relative">
                    <div className="font-semibold">
                        TP Eventos
                        {rolPrincipal && (
                            <span className="ml-2 text-xs bg-blue-600 px-2 py-1 rounded">
                                {rolPrincipal === RolUsuario.ADMIN
                                    ? 'Admin'
                                    : rolPrincipal === RolUsuario.ORGANIZADOR
                                      ? 'Organizador'
                                      : 'Usuario'}
                            </span>
                        )}
                    </div>
                    <nav className="hidden gap-3 md:flex">
                        <Link to="/eventos" className="hover:underline">
                            Eventos
                        </Link>

                        {/* Solo mostrar crear evento si es organizador o admin */}
                        {(esOrg || esSysAdmin) && (
                            <Link
                                to="/organizador/eventos/nuevo"
                                className="hover:underline"
                            >
                                Crear evento
                            </Link>
                        )}

                        {/* Mis inscripciones solo para usuarios normales */}
                        {!esOrg && !esSysAdmin && (
                            <Link
                                to="/mis-inscripciones"
                                className="hover:underline"
                            >
                                Mis inscripciones
                            </Link>
                        )}

                        {/* Mis eventos para organizadores, todos los eventos para admin */}
                        {(esOrg || esSysAdmin) && (
                            <Link
                                to="/organizador/mis-eventos"
                                className="hover:underline"
                            >
                                {esSysAdmin
                                    ? 'Todos los eventos'
                                    : 'Mis eventos'}
                            </Link>
                        )}

                        {/* Solo mostrar admin si es admin */}
                        {esSysAdmin && (
                            <Link to="/admin" className="hover:underline">
                                Admin
                            </Link>
                        )}
                    </nav>
                    
                    <div className="flex items-center gap-4">
                        <NotificationDropdown />
                        
                        <div className="relative">
                            <button
                                className="ml-2 rounded-full bg-white/15 px-3 py-1 text-sm hover:bg-white/25"
                                onClick={() => setOpen((v) => !v)}
                            >
                                {usuario.email}
                            </button>

                            {open && (
                                <div className="absolute right-0 mt-2 w-48 rounded-md bg-white text-slate-900 shadow-lg">
                                    <div className="flex flex-col p-2 gap-2">
                                        <div className="px-3 py-1 text-sm text-gray-600 border-b">
                                            {usuario.email}
                                        </div>
                                        <button
                                            onClick={handleLogout}
                                            className="rounded border px-3 py-1 text-left hover:bg-gray-100"
                                        >
                                            Cerrar sesión
                                        </button>
                                    </div>
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </header>
    )
}
