import { type SubmitHandler, useForm } from 'react-hook-form'
import { type InputCrearEvento, SchemaCrearEvento } from '../../lib/schemas'
import api from '../../lib/api'
import { zodResolver } from '@hookform/resolvers/zod'
import { toast } from 'react-toastify'
import { type ErrorDelServidor, esErrorDelServidor } from '../../tipos'
import ContainerDeToast from '../ContainerDeToast'

export default function CreacionEvento() {
    const {
        register,
        handleSubmit,
        formState: { errors },
        getValues,
        reset,
    } = useForm<InputCrearEvento>({
        resolver: zodResolver(SchemaCrearEvento),
    })

    const onSubmit: SubmitHandler<InputCrearEvento> = (
        data: InputCrearEvento
    ) =>
        api
            .post('/api/v1/evento', data)
            .then((_) => {
                toast.success(`Se ha creado el evento '${getValues('titulo')}'`)
                reset()
            })
            .catch((error) => {
                if (esErrorDelServidor(error.response.data)) {
                    let errorDelServidor: ErrorDelServidor = error.response.data
                    let primerError = errorDelServidor.errores[0]
                    toast.error(
                        `Error con el campo ${primerError.campo}: ${primerError.mensaje}`
                    )
                }
            })

    /* TODO: si me dan ganas, hacer que la fecha del evento deba estar en el futuro (tendría que cambiar el schema de zod, el html, y el back end) */
    // TODO: si me dan ganas, hacer que se valide al cambiar de campo
    return (
        <div className="mx-auto max-w-6xl px-4 py-6">
            <h1 className="text-xl font-semibold text-slate-900">
                Crear evento
            </h1>
            <form
                onSubmit={handleSubmit(onSubmit)}
                className="mt-4 rounded-lg bg-white p-4"
            >
                <div className="flex flex-col gap-3">
                    <div className="flex flex-col gap-1">
                        <label
                            htmlFor="titulo"
                            className="text-sm font-medium text-slate-700"
                        >
                            Título
                        </label>
                        <input
                            type="text"
                            id="titulo"
                            {...register('titulo')}
                            className="w-full rounded-md border border-slate-300 px-3 py-2 shadow-sm placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500"
                            placeholder="Título"
                        />
                        {
                            <p
                                className="mt-1 text-sm text-red-600"
                                role="alert"
                            >
                                {errors.titulo?.message}
                            </p>
                        }
                    </div>

                    <div className="flex flex-col gap-1">
                        <label
                            htmlFor="descripcion"
                            className="text-sm font-medium text-slate-700"
                        >
                            Descripción
                        </label>
                        <textarea
                            id="descripcion"
                            rows={3}
                            {...register('descripcion')}
                            className="w-full rounded-md border border-slate-300 px-3 py-2 shadow-sm placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500 resize-y min-h-32"
                            placeholder="Descripción"
                        />
                        {
                            <p
                                className="mt-1 text-sm text-red-600"
                                role="alert"
                            >
                                {errors.descripcion?.message}
                            </p>
                        }
                    </div>

                    <div className="flex flex-col gap-1">
                        <label
                            htmlFor="fechaHoraInicio"
                            className="text-sm font-medium text-slate-700"
                        >
                            Fecha y hora de inicio
                        </label>
                        <input
                            type="datetime-local"
                            id="fechaHoraInicio"
                            {...register('fechaHoraInicio')}
                            className="w-full rounded-md border border-slate-300 px-3 py-2 shadow-sm placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500"
                        />
                        {
                            <p
                                className="mt-1 text-sm text-red-600"
                                role="alert"
                            >
                                {errors.fechaHoraInicio?.message}
                            </p>
                        }
                    </div>

                    <div className="flex flex-col gap-1">
                        <label
                            htmlFor="duracionMinutos"
                            className="text-sm font-medium text-slate-700"
                        >
                            Duración (minutos)
                        </label>
                        <input
                            type="number"
                            id="duracionMinutos"
                            {...register('duracionMinutos', {
                                valueAsNumber: true,
                            })}
                            className="w-full rounded-md border border-slate-300 px-3 py-2 shadow-sm placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500"
                            placeholder="Duración (minutos)"
                        />
                        {
                            <p
                                className="mt-1 text-sm text-red-600"
                                role="alert"
                            >
                                {errors.duracionMinutos?.message}
                            </p>
                        }
                    </div>

                    <div className="flex flex-col gap-1">
                        <label
                            htmlFor="ubicacion"
                            className="text-sm font-medium text-slate-700"
                        >
                            Ubicación
                        </label>
                        <input
                            type="text"
                            id="ubicacion"
                            {...register('ubicacion')}
                            className="w-full rounded-md border border-slate-300 px-3 py-2 shadow-sm placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500"
                            placeholder="Ubicación"
                        />
                        {
                            <p
                                className="mt-1 text-sm text-red-600"
                                role="alert"
                            >
                                {errors.ubicacion?.message}
                            </p>
                        }
                    </div>

                    <div className="flex flex-col gap-1">
                        <label
                            htmlFor="cupoMaximo"
                            className="text-sm font-medium text-slate-700"
                        >
                            Cupo máximo
                        </label>
                        <input
                            type="number"
                            id="cupoMaximo"
                            {...register('cupoMaximo', { valueAsNumber: true })}
                            className="w-full rounded-md border border-slate-300 px-3 py-2 shadow-sm placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500"
                            placeholder="Cupo máximo"
                        />
                        {
                            <p
                                className="mt-1 text-sm text-red-600"
                                role="alert"
                            >
                                {errors.cupoMaximo?.message}
                            </p>
                        }
                    </div>

                    <div className="flex flex-col gap-1">
                        <label
                            htmlFor="precio"
                            className="text-sm font-medium text-slate-700"
                        >
                            Precio
                        </label>
                        <input
                            type="number"
                            id="precio"
                            {...register('precio', { valueAsNumber: true })}
                            className="w-full rounded-md border border-slate-300 px-3 py-2 shadow-sm placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500"
                            placeholder="Precio"
                        />
                        {
                            <p
                                className="mt-1 text-sm text-red-600"
                                role="alert"
                            >
                                {errors.precio?.message}
                            </p>
                        }
                    </div>

                    <div className="flex flex-col gap-1">
                        <label
                            htmlFor="categoria"
                            className="text-sm font-medium text-slate-700"
                        >
                            Categoría
                        </label>
                        <input
                            type="text"
                            id="categoria"
                            {...register('categoria')}
                            className="w-full rounded-md border border-slate-300 px-3 py-2 shadow-sm placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500"
                            placeholder="Categoría"
                        />
                        {
                            <p
                                className="mt-1 text-sm text-red-600"
                                role="alert"
                            >
                                {errors.categoria?.message}
                            </p>
                        }
                    </div>

                    <ContainerDeToast />

                    <div className="mt-2 flex justify-end">
                        <button
                            type="submit"
                            className="rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                        >
                            Crear
                        </button>
                    </div>
                </div>
            </form>
        </div>
    )
}
