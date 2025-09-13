import { useEffect, useRef, useState } from "react";
import type { Evento } from "../../tipos";

export default function EventoActionMenu(props: {evento: Evento, className?: string}) {
    const [open, setOpen] = useState(false)
    const menuRef = useRef<HTMLDivElement | null>(null)
    const evento = props.evento

    useEffect(() => {
        const handler = (e: MouseEvent) => {
            if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
                setOpen(false);
            }
        };
        document.addEventListener("mousedown", handler);
        return () => document.removeEventListener("mousedown", handler);
    }, []);

    return (
        <div className={`${props.className || ""} relative inline-block text-left`} ref={menuRef}>
        {/* Button */}
        <button
            onClick={() => setOpen(!open)}
            className="p-2 text-white rounded-lg hover:bg-gray-200 transition"
        >
            <i className="fa-solid fa-ellipsis text-black"></i>
        </button>

        {/* Dropdown */}
        <div className={`absolute z-10 left-1/2 -translate-x-1/2 mt-2 w-40 min-w-fit border border-gray-200
            bg-white rounded-lg origin-top transition transform duration-200
            ${open ? "opacity-100 scale-100 translate-y-0" : "opacity-0 scale-95 -translate-y-1 pointer-events-none"}`}>

            <ul className="text-gray-700">
                <li>
                <a className="block w-full text-center px-4 py-2 hover:bg-gray-100"
                    href={`/eventos/${evento.id}`}> {/* TODO: Cambiar cuando esté esta ruta definida*/}
                    Ver detalle
                </a>
                </li>
                <li>
                <a className="block w-full text-center px-4 py-2 hover:bg-gray-100"
                    href={`/eventos/${evento.id}/gestionar-inscriptos`}> {/* TODO: Cambiar cuando esté esta ruta definida*/} 
                    Gestionar inscriptos
                </a>
                </li>
            </ul>
            </div>
        </div>
    );
}