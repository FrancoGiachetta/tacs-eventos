import { useEffect, useState } from 'react'
import api from '../../lib/api'
import type { Evento } from "../../types/evento"
import type { Inscripcion, ItemWaitlist } from "../../types/inscripciones"
import { formatDate } from '../../lib/utils'
import EventoActionMenu from './EventoActionMenu'

export default function MisEventos() {
    let [eventos, setEventos] = useState<Evento[]>([])

    useEffect(() => {
        api.get('/api/v1/usuario/mis-eventos').then((r) => {setEventos(r.data)})
    }, [])

    return (
        <>
            <div className="mt-5">
                <h1 className="text-3xl mb-7">Mis eventos</h1>
                <table className="table-auto border-collapse min-w-full text-sm text-left rounded-lg">
                    <thead>
                        <tr className="table-row font-bold bg-gray-200 text-gray-600">
                            <td className="px-4 py-2">Nombre</td>
                            <td className="px-4 py-2">Fecha</td>
                            <td className="px-4 py-2">Ubicacion</td>
                            <td className="px-4 py-2">Abierto</td>
                            <td className="px-4 py-2">Inscriptos</td>
                            <td className="px-4 py-2">En Waitlist</td>
                            <td className="px-4 py-2">{'Acción'}</td>
                        </tr>
                    </thead>
                    <tbody>
                        {eventos.map((e: Evento) => {
                            return <EventoRow key={e.id} {...e} />
                        })}
                    </tbody>
                </table>
            </div>
        </>
    )
}

function EventoRow(evento: Evento) {
    let [inscripciones, setInscripciones] = useState<Inscripcion[]>([])
    let [waitlist, setWaitlist] = useState<ItemWaitlist[]>([])

    useEffect(() => {
        let insAux: Inscripcion[] = []
        let waitAux: ItemWaitlist[] = []
        api.get(`/api/v1/evento/${evento.id}/inscripcion`).then((r) => {
            insAux = r.data
        })
        api.get(`/api/v1/evento/${evento.id}/waitlist`).then((r) => {
            waitAux = r.data
        })
        setInscripciones(insAux)
        setWaitlist(waitAux)
    }, [])

    return (
        <tr className="table-row even:bg-gray-100 odd:bg-gray-50">
            <td className="px-4 py-2">{evento.titulo}</td>
            <td className="px-4 py-2">
                {formatDate(new Date(evento.fechaHoraInicio), {
                    withTime: true,
                })}
            </td>
            <td className="px-4 py-2">{evento.ubicacion}</td>
            <td className="px-4 py-2">{evento.abierto ? 'SÍ' : 'No'}</td>
            <td className="px-4 py-2">
                {inscripciones.length}/{evento.cupoMaximo}
            </td>
            <td className="px-4 py-2">{waitlist.length}</td>
            <td className="px-4 py-2">
                <EventoActionMenu evento={evento} className="bg-transparent" />
            </td>
        </tr>
    )
}
