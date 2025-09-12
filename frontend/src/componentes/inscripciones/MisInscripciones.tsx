import { useEffect, useState, type ReactNode } from "react"
import api from "../../lib/api"
import InscripcionItem from "./InscripcionItem"
import type { Inscripcion, Evento } from "../../tipos"


export default function MisInscripciones() {
    
    const [inscripciones, setInscripciones] = useState<Inscripcion[]>([])
    const [loaded, setLoaded] = useState(false)

    useEffect(() => {
        api.get("/api/v1/usuario/mis-inscripciones")
          .then(r => {
            const data = r.data
            const inscripcionesIds: Inscripcion[] = data.map((i: any) => {return {eventoId: i.eventoId, estado: i.estado}})
            setInscripciones(inscripcionesIds)
            setLoaded(true)
          })
    }, [])

    return (
        <div className="mt-10 w-[90%] mx-auto">
            <h1 className="text-4xl">Mis inscripciones</h1>
            <div className="mt-5 w-full mx-auto rounded-lg bg-gray-100 p-10 flex flex-col gap-4">
                {loaded && (inscripciones.length > 0 ? inscripciones.map(
                    (inscripcion: Inscripcion) => (
                    <InscripcionItem  {...inscripcion}/>
                    ))
                :
                    <h3 className="text-3xl text-gray-600 font-bold text-center m-auto my-20">
                        No est{'á'}s inscripto a ning{'ú'}n evento actualmente.
                    </h3>
                )}
            </div>
        </div>
    )
}