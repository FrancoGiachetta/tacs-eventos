import { Link, useNavigate } from 'react-router-dom'
import { useState } from 'react'
import api from '../lib/api'

export default function BarraSuperior() {
    const [open, setOpen] = useState(false)
    const navigate = useNavigate()
    const token = localStorage.getItem('token')

    const handleLogout = async () => {
        try {
            await api.post(
                '/api/v1/auth/logout',
                {},
                {
                    headers: {
                        'X-Session-Token': token || '',
                    },
                }
            )
        } catch (e) {
            // Ignorar error
        }
        localStorage.removeItem('token')
        navigate('/login')
    }

    return (
        <header className="sticky top-0 z-40 bg-blue-400 text-white">
            <div className="mx-auto max-w-6xl px-4">
                <div className="flex h-14 items-center justify-between relative">
                    <div className="font-semibold">TP Eventos</div>
                    <nav className="hidden gap-3 md:flex">
                        <Link to="/eventos" className="hover:underline">
                            Eventos
                        </Link>
                        <Link
                            to="/organizador/eventos/nuevo"
                            className="hover:underline"
                        >
                            Crear evento
                        </Link>
                        <Link
                            to="/mis-inscripciones"
                            className="hover:underline"
                        >
                            Mis inscripciones
                        </Link>
                        <Link to="/admin" className="hover:underline">
                            Admin
                        </Link>
                    </nav>
                    <div className="relative">
                        <button
                            className="ml-2 rounded-full bg-white/15 px-3 py-1 text-sm hover:bg-white/25"
                            onClick={() => setOpen((v) => !v)}
                        >
                            {token ? 'Usuario' : 'Elegir usuario'}
                        </button>

                        {open && (
                            <div className="absolute right-0 mt-2 w-48 rounded-md bg-white text-slate-900 shadow-lg">
                                <div className="flex flex-col p-2 gap-2">
                                    {!token ? (
                                        <>
                                            <Link
                                                to="/login"
                                                onClick={() => setOpen(false)}
                                                className="rounded border px-3 py-1 text-left"
                                            >
                                                Iniciar sesión
                                            </Link>
                                            <Link
                                                to="/registro"
                                                onClick={() => setOpen(false)}
                                                className="rounded border px-3 py-1 text-left"
                                            >
                                                Registrarse
                                            </Link>
                                        </>
                                    ) : (
                                        <button
                                            onClick={handleLogout}
                                            className="rounded border px-3 py-1 text-left"
                                        >
                                            Salir
                                        </button>
                                    )}
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </header>
    )
}
