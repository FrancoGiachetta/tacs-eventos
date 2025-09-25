import { useEffect, useState } from 'react'
import api from '../../lib/api'
import BarraSuperior from '../BarraSuperior'
import { Evento } from '../../types/evento'
import FormularioEvento from './FormularioEvento'
import { toast } from '../../lib/simpleToast'
import { useParams, useNavigate } from 'react-router-dom'
import { useAuth } from '../../contexts/AuthContext'
import { esAdmin, esOrganizador } from '../../types/usuario'

export default function DetalleEvento() {
    let eventoId = useParams().eventoId
    const { usuario } = useAuth()
    const navigate = useNavigate()

    let [eventoInfo, setEventoInfo] = useState<Evento | null>(null)

    useEffect(() => {
        api.get(`/api/v1/evento/${eventoId}`).then((res) => {
            const info: Evento = res.data
            setEventoInfo(info)
        })
    }, [eventoId])

    const onInscribir = async () => {
        if (!usuario?.id) {
            toast.error('Debe estar logueado para inscribirse')
            return
        }

        try {
            const response = await api.post(
                `/api/v1/evento/${eventoId}/inscripcion/${usuario.id}`
            )

            // Status 201 = nueva inscripción creada
            // Status 303 = ya estaba inscripto (redirigido a inscripción existente)
            if (response.status === 201) {
                toast.success('Inscripción realizada correctamente')
                // Redirigir a mis inscripciones después de inscripción exitosa
                setTimeout(() => {
                    navigate('/mis-inscripciones')
                }, 1500) // Esperar 1.5s para que el usuario vea el mensaje
            } else if (response.status === 303) {
                toast.info('Ya estás inscripto a este evento')
            } else {
                toast.success('Inscripción confirmada')
                // Redirigir también en caso de otras respuestas exitosas
                setTimeout(() => {
                    navigate('/mis-inscripciones')
                }, 1500)
            }
        } catch (e: any) {
            // Axios trata 303 como error por defecto, así que manejamos también aquí
            if (e.response?.status === 303) {
                toast.info('Ya estás inscripto a este evento')
            } else {
                toast.error('No se pudo realizar la inscripción')
            }
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

                {/* Solo mostrar botón de inscribirse para usuarios normales (no organizadores ni admins) */}
                {!esAdmin(usuario) && !esOrganizador(usuario) && (
                    <div className="mt-4 flex justify-end">
                        <button
                            type="button"
                            onClick={onInscribir}
                            className="rounded bg-emerald-600 px-4 py-2 text-white hover:bg-emerald-700 focus:outline-none focus:ring-2 focus:ring-emerald-500"
                        >
                            Inscribirse
                        </button>
                    </div>
                )}
            </div>
        </>
    ) : null
}
