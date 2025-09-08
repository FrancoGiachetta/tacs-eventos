import Card from '@mui/material/Card'
import Box from '@mui/material/Box'
import Skeleton from '@mui/material/Skeleton'

import { useEffect, useState } from 'react'
import api from '../../lib/api'
import BarraSuperior from '../BarraSuperior'

interface EventoInfo {}

function DetalleEvento(eventoId: string) {
    let [eventoInfo, setEventoInfo] = useState<EventoInfo | null>(null)

    useEffect(() => {
        api.get(`/api/v1/evento/${eventoId}`).then((res) => {
            const info: EventoInfo = res.data
            setEventoInfo(info)
        })
    }, [])

    return eventoInfo ? (
        <Box>
            <BarraSuperior />
            <Card></Card>
        </Box>
    ) : (
        <Skeleton variant="rectangular" width={210} height={118} />
    )
}

export default DetalleEvento
