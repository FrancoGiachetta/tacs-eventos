import { Link } from 'react-router-dom'
import { useState } from 'react'
import { Usuario } from "@/tipos";

export default function BarraSuperior() {
    const [open, setOpen] = useState(false)
    const user: Usuario = null // todo: cambiar por useAuth()

    return (
        <header className="sticky top-0 z-40 bg-blue-400 text-white">
            <div className="mx-auto max-w-6xl px-4">
                <div className="flex h-14 items-center justify-between relative">
                    <div className="font-semibold">TP Eventos</div>
                    <nav className="hidden gap-3 md:flex">
                        <Link to="/" className="hover:underline">Eventos</Link>
                        <Link to="/organizador/new" className="hover:underline">Crear evento</Link>
                        <Link to="/mis-inscripciones" className="hover:underline">Mis inscripciones</Link>
                        <Link to="/admin" className="hover:underline">Admin</Link>
                    </nav>
                    <div className="relative">
                        <button
                            className="ml-2 rounded-full bg-white/15 px-3 py-1 text-sm hover:bg-white/25"
                            onClick={() => setOpen(v => !v)}
                        >
                            {user ? user.name : 'Elegir usuario'}
                        </button>

                        {open && (
                            <div className="absolute right-0 mt-2 w-48 rounded-md bg-white text-slate-900 shadow-lg">
                                {!user ? (
                                    <div className="flex flex-col p-2 gap-2">
                                        <button onClick={() => setOpen(false)} className="rounded border px-3 py-1 text-left">Iniciar sesión</button>
                                        <button onClick={() => setOpen(false)} className="rounded border px-3 py-1 text-left">Registrarse</button>
                                    </div>
                                ) : (
                                    <div className="flex flex-col p-2 gap-2">
                                        <span className="text-sm">{user.name} — {user.role}</span>
                                        <button onClick={() => setOpen(false)} className="rounded border px-3 py-1 text-left">Salir</button>
                                    </div>
                                )}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </header>
    )
}
