import { useEffect, useState, type ReactNode } from "react"
import api from "../../lib/api"
import { formatDate } from "../../lib/utils"
import { DialogoConfirmar } from "../../componentes/DialogoConfirmar"

type InscripcionResponse = {
    eventoId: string,
    estado: string,
}

type EventoResponse = {
  titulo: string,
  descripcion: string,
  fechaHoraInicio: string
  duracionMinutos: number,
  ubicacion: string,
  cupoMaximo: number,
  precio: number,
  categoria: string
}

const eventos: EventoResponse[] = [{
  titulo: "Test test 1",
  descripcion: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin quam dui, malesuada at congue sed, pellentesque ut leo. Sed pellentesque consectetur iaculis. Phasellus pulvinar lacus vel mattis gravida. Maecenas ut sodales eros. Nullam dolor neque, tempor non suscipit at, vehicula sit amet nulla. In hac habitasse platea dictumst.",
  fechaHoraInicio: "2025-10-10 15:00:00.0000Z",
  duracionMinutos: 120,
  ubicacion: "Jorge Gascon 1900",
  cupoMaximo: 100,
  precio: 400000,
  categoria: "Deportes"
},
{
  titulo: "Test test 2",
  descripcion: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin quam dui, malesuada at congue sed, pellentesque ut leo. Sed pellentesque consectetur iaculis. Phasellus pulvinar lacus vel mattis gravida. Maecenas ut sodales eros. Nullam dolor neque, tempor non suscipit at, vehicula sit amet nulla. In hac habitasse platea dictumst.",
  fechaHoraInicio: "2025-10-10 15:00:00.0000Z",
  duracionMinutos: 120,
  ubicacion: "Jorge Gascon 1900",
  cupoMaximo: 100,
  precio: 400000,
  categoria: "Deportes"
},
{
  titulo: "Test test 3",
  descripcion: "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin quam dui, malesuada at congue sed, pellentesque ut leo. Sed pellentesque consectetur iaculis. Phasellus pulvinar lacus vel mattis gravida. Maecenas ut sodales eros. Nullam dolor neque, tempor non suscipit at, vehicula sit amet nulla. In hac habitasse platea dictumst.",
  fechaHoraInicio: "2025-10-10 15:00:00.0000Z",
  duracionMinutos: 120,
  ubicacion: "Jorge Gascon 1900",
  cupoMaximo: 100,
  precio: 400000,
  categoria: "Deportes"
},
]

function InscripcionItem(props: InscripcionResponse): ReactNode {
    const { eventoId, estado } = props
    const [evento, setEvento] = useState<EventoResponse | null>(null)

    const [dialogoCancelarAbierto, setDialogoCancelarAbierto] = useState(false);

    const confirmarCancelacion = async () => {
        try {
            const res = await api.delete(`/api/v1/evento/${eventoId}`)
            document.location.reload()
        } catch (e: any) {
            alert("No fue posible cancelar la inscripción")
        } finally {
            setDialogoCancelarAbierto(false)
        }
    }

    useEffect(() => {
        // api.get(`/api/v1/evento/${eventoId}`)
        api.get(`/api/v1/evento`)
          .then(r => {
            // const eventoDatos: EventoResponse = r.data
            const eventoDatos = eventos[Number.parseInt(eventoId)]
            setEvento(eventoDatos)
          })
    }, [])

    return (<>
        {dialogoCancelarAbierto && <DialogoConfirmar 
            titulo={"Cancelar inscripción"}
            mensaje={`¿Está seguro que desea cancelar su inscripción al evento "${evento?.titulo}"`}
            onConfirm={confirmarCancelacion}
            onCancel={() => {setDialogoCancelarAbierto(false)}}
        />}
        <div className={`bg-white rounded-md w-full mx-auto shadow-md border-gray border border-gray-200 p-6 
                    
                     ${!dialogoCancelarAbierto ? "hover:shadow-lg hover:scale-[1.01]" : "duration-0"} transition duration-300`}>
            {evento && <>

            <div className="flex flex-row justify-between items-center gap-4">
                <h3 className="text-2xl font-semibold">{evento.titulo || ""}</h3>
                <EstadoInscripcionBadge estado={estado} />
            </div>
            <div className="flex flex-row justify-between items-center gap-4">
                <div>
                    <p className="text-gray-500">
                        <span><i className="fa-regular fa-calendar"></i></span>
                        {formatDate(new Date(evento.fechaHoraInicio), {withTime: true}) || ""}
                        <span><i className="ml-4 fa-regular fa-clock"></i></span>
                        {evento.duracionMinutos + "min"}
                    </p>
                    <p className="text-gray-500">
                        <span><i className="fa-solid fa-location-dot"></i></span>
                        {evento.ubicacion}
                    </p>
                    <p className="text-gray-700 text-base leading-relaxed">{evento.descripcion || ""}</p>
                </div>
                <div className="flex flex-row gap-2">
                    <button className="rounded-md p-2 h-min hover:bg-blue-500 hover:text-white transition duration-300">
                        <a href={`/api/v1/evento/${eventoId}`} className="flex flex-col justify-center">
                        <span><i className="fa-solid fa-info"></i></span>
                        Detalles
                        </a>
                    </button>
                    <button className="flex flex-col justify-center rounded-md p-2 h-min hover:bg-red-600 hover:text-white transition duration-300"
                            onClick={() => {
                                setDialogoCancelarAbierto(true)
                            }}>
                        <span><i className="fa-solid fa-xmark"></i></span>
                        Cancelar
                    </button>
                </div>
            </div>
            </>}
        </div>
    </>)
}

function EstadoInscripcionBadge(props: {estado: string}) {
    const {estado} = props

    const confirmadaStyles = "px-2 py-1 border-green-950 border-2 bg-green-400 text-green-950"
    const pendienteStyles = "px-2 py-1 border-yellow-950 border-2 bg-yellow-400 text-yellow-950"
    const canceladaStyles = "px-2 py-1 border-red-950 border-2 bg-red-400 text-red-950"

    let styles = "";
    let text = "";
    switch (estado) {
        case "CONFIRMADA":
            styles = confirmadaStyles
            text = "Confirmada"
        break;
        case "PENDIENTE":
            styles = pendienteStyles
            text = "En lista de espera"
        break;
        case "CANCELADA":
            styles = canceladaStyles
            text = "Pendiente"
            text = "Cancelada"
        break;
    }

    return <>
        <div className={`${styles} rounded-full mx-1`}>
            <p>{text}</p>
        </div>
    </>

}

export default function MisInscripciones() {
    
    const [inscripciones, setInscripciones] = useState<InscripcionResponse[]>([])
    const [loaded, setLoaded] = useState(false)

    useEffect(() => {
        api.get("/api/v1/usuario/mis-inscripciones")
          .then(r => {
            const data = r.data
            const inscripcionesIds: InscripcionResponse[] = data.map((i: any) => {return {eventoId: i.eventoId, estado: i.estado}})
            // setInscripciones(inscripcionesIds)
            setInscripciones([
                {eventoId: "0", estado: "CONFIRMADA"},
                {eventoId: "1", estado: "PENDIENTE"},
                {eventoId: "2", estado: "CANCELADA"}
            ])
            setLoaded(true)
          })
    }, [])

    return (
        <div className="mt-10 w-[90%] mx-auto">
            <h1 className="text-4xl">Mis inscripciones</h1>
            <div className="mt-5 w-full mx-auto rounded-lg bg-gray-100 p-10 flex flex-col gap-4">
                {loaded && (inscripciones.length > 0 ? inscripciones.map(
                    (inscripcion: {eventoId: string, estado: string}) => (
                    <InscripcionItem eventoId={inscripcion.eventoId} estado={inscripcion.estado} />
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