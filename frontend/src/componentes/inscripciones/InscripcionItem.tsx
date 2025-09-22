import { useState, useEffect } from 'react'
import type { ReactNode } from 'react'
import api from '../../lib/api'
import { formatDate } from '../../lib/utils'
import { DialogoConfirmar } from '../DialogoConfirmar'
import type { Evento } from '../../types/evento'
import type { Inscripcion } from '../../types/inscripciones'

export default function InscripcionItem(props: Inscripcion): ReactNode {
    const { eventoId, estado } = props
    const [evento, setEvento] = useState<Evento | null>(null)

    const [dialogoCancelarAbierto, setDialogoCancelarAbierto] = useState(false)

    const confirmarCancelacion = async () => {
        try {
            // Obtener el ID del usuario actual desde el token o contexto
            const userResponse = await api.get('/api/v1/usuario/me')
            const userId = userResponse.data.id
            
            const res = await api.delete(`/api/v1/evento/${eventoId}/inscripcion/${userId}`)
            document.location.reload()
        } catch (e: any) {
            alert('No fue posible cancelar la inscripción')
        } finally {
            setDialogoCancelarAbierto(false)
        }
    }

    useEffect(() => {
        api.get(`/api/v1/evento/${eventoId}`).then((r) => {
            const eventoDatos: Evento = r.data
            setEvento(eventoDatos)
        })
    }, [])

    return (
        <>
            {dialogoCancelarAbierto && (
                <DialogoConfirmar
                    titulo={'Cancelar inscripción'}
                    mensaje={`¿Está seguro que desea cancelar su inscripción al evento "${evento?.titulo}"`}
                    onConfirm={confirmarCancelacion}
                    onCancel={() => {
                        setDialogoCancelarAbierto(false)
                    }}
                />
            )}
            <div
                className={`bg-white rounded-md w-full mx-auto shadow-md border-gray border border-gray-200 p-6 
                    
                     ${!dialogoCancelarAbierto ? 'hover:shadow-lg hover:scale-[1.01]' : 'duration-0'} transition duration-300`}
            >
                {evento && (
                    <>
                        <div className="flex flex-row justify-between items-center gap-4">
                            <h3 className="text-2xl font-semibold">
                                {evento.titulo || ''}
                            </h3>
                            <EstadoInscripcionBadge estado={estado} />
                        </div>
                        <div className="flex flex-row justify-between items-center gap-4">
                            <div>
                                <p className="text-gray-500">
                                    <span>
                                        <i className="fa-regular fa-calendar"></i>
                                    </span>
                                    {formatDate(
                                        new Date(evento.fechaHoraInicio),
                                        { withTime: true }
                                    ) || ''}
                                    <span>
                                        <i className="ml-4 fa-regular fa-clock"></i>
                                    </span>
                                    {evento.duracionMinutos + 'min'}
                                </p>
                                <p className="text-gray-500">
                                    <span>
                                        <i className="fa-solid fa-location-dot"></i>
                                    </span>
                                    {evento.ubicacion}
                                </p>
                                <p className="text-gray-700 text-base leading-relaxed">
                                    {evento.descripcion || ''}
                                </p>
                            </div>
                            <div className="flex flex-row gap-2">
                                <button className="rounded-md p-2 h-min hover:bg-blue-500 hover:text-white transition duration-300">
                                    <a
                                        href={`/eventos/${eventoId}`}
                                        className="flex flex-col justify-center"
                                    >
                                        <span>
                                            <i className="fa-solid fa-info"></i>
                                        </span>
                                        Detalles
                                    </a>
                                </button>
                                <button
                                    className="flex flex-col justify-center rounded-md p-2 h-min hover:bg-red-600 hover:text-white transition duration-300"
                                    onClick={() => {
                                        setDialogoCancelarAbierto(true)
                                    }}
                                >
                                    <span>
                                        <i className="fa-solid fa-xmark"></i>
                                    </span>
                                    Cancelar
                                </button>
                            </div>
                        </div>
                    </>
                )}
            </div>
        </>
    )
}

function EstadoInscripcionBadge(props: { estado: string }) {
    const { estado } = props

    const confirmadaStyles =
        'px-2 py-1 border-green-950 border-2 bg-green-400 text-green-950'
    const pendienteStyles =
        'px-2 py-1 border-yellow-950 border-2 bg-yellow-400 text-yellow-950'
    const canceladaStyles =
        'px-2 py-1 border-red-950 border-2 bg-red-400 text-red-950'

    let styles = ''
    let text = ''
    switch (estado) {
        case 'CONFIRMADA':
            styles = confirmadaStyles
            text = 'Confirmada'
            break
        case 'PENDIENTE':
            styles = pendienteStyles
            text = 'En lista de espera'
            break
        case 'CANCELADA':
            styles = canceladaStyles
            text = 'Cancelada'
            break
    }

    return (
        <>
            <div className={`${styles} rounded-full mx-1`}>
                <p>{text}</p>
            </div>
        </>
    )
}
