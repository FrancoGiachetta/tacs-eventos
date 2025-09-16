import { useEffect, useState } from 'react'
import api from '../../lib/api'
import BarraSuperior from '../BarraSuperior'
import {Evento} from "../../types/evento";

function DetalleEvento(eventoId: string) {
    let [eventoInfo, setEventoInfo] = useState<Evento | null>(null)

    useEffect(() => {
        api.get(`/api/v1/evento/${eventoId}`).then((res) => {
            const info: Evento = res.data
            setEventoInfo(info)
        })
    }, [])

    return eventoInfo ? (

    )
}

export default DetalleEvento
