import { useEffect, useState } from 'react'
import api from '../../lib/api'
import BarraSuperior from '../BarraSuperior'
import { Evento } from '../../types/evento'
import FormularioEvento from './FormularioEvento'
import { toast } from '../../lib/simpleToast'
import { useParams } from 'react-router-dom'

export default function DetalleEvento() {
    let eventoId = useParams().eventoId

    let [eventoInfo, setEventoInfo] = useState<Evento | null>(null)

    useEffect(() => {
        api.get(`/api/v1/evento/${eventoId}`).then((res) => {
            const info: Evento = res.data
            setEventoInfo(info)
        })
    }, [eventoId])

    const onInscribir = async () => {
        try {
            await api.post(`/api/v1/evento/${eventoId}/inscripcion/`)
            toast.success('Inscripción realizada correctamente')
        } catch (e) {
            toast.error('No se pudo realizar la inscripción')
        }
    }

    return eventoInfo ? (
        <>
            <div className="mx-auto max-w-6xl px-4 py-6">
                <FormularioEvento
                    id={eventoInfo.id}
                    valoresPorDefecto={eventoInfo}
                    visualizar={true}
                />

                <div className="mt-4 flex justify-end">
                    <button
                        type="button"
                        onClick={onInscribir}
                        className="rounded bg-emerald-600 px-4 py-2 text-white hover:bg-emerald-700 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                    >
                        Inscribirse
                    </button>
                </div>
            </div>
        </>
    ) : null
}
