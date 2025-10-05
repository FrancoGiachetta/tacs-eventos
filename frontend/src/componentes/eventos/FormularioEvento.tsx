import { type SubmitHandler, useForm } from 'react-hook-form'
import { type InputCrearEvento, SchemaCrearEvento } from '../../lib/schemas'
import api from '../../lib/api'
import { zodResolver } from '@hookform/resolvers/zod'
import { toast } from '../../lib/simpleToast'
import { type ErrorDelServidor, esErrorDelServidor } from '../../types/errores'
import { CATEGORIAS_EVENTO } from '../../types/categorias'
import { useAuth } from '../../contexts/AuthContext'
import { esAdmin } from '../../types/usuario'

interface Props {
    id?: String
    valoresPorDefecto?: Partial<InputCrearEvento>
    visualizar?: boolean
}

export default function FormularioEvento({
    id,
    valoresPorDefecto,
    visualizar = false,
}: Props) {
    const { usuario } = useAuth()
    const {
        register,
        handleSubmit,
        formState: { errors },
        getValues,
        reset,
        setValue,
    } = useForm<InputCrearEvento>({
        resolver: zodResolver(SchemaCrearEvento),
        defaultValues: valoresPorDefecto,
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
            {visualizar ? (
                <h1 className="text-xl font-semibold text-slate-900">
                    {getValues('titulo')}
                </h1>
            ) : (
                <h1 className="text-xl font-semibold text-slate-900">
                    Crear evento
                </h1>
            )}
            <form
                onSubmit={handleSubmit(onSubmit)}
                className="mt-4 rounded-lg bg-white p-4"
            >
                <div className="flex flex-col gap-3">
                    {id && esAdmin(usuario) && (
                        <div className="flex flex-col gap-1">
                            <label
                                htmlFor="idEvento"
                                className="text-sm font-medium text-slate-700"
                            >
                                Id
                            </label>
                            <input
                                type="text"
                                id="idEvento"
                                value={id?.toString()}
                                readOnly
                                className="w-full rounded-md border border-slate-300 px-3 py-2 shadow-sm bg-slate-100 text-slate-700"
                            />
                        </div>
                    )}

                    {!visualizar && (
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
                                placeholder="Ej: Conferencia de Tecnología 2025"
                                maxLength={100}
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
                    )}

                    <div className="flex flex-col gap-1">
                        <label
                            htmlFor="descripcion"
                            className="text-sm font-medium text-slate-700"
                        >
                            Descripción
                        </label>
                        <textarea
                            id="descripcion"
                            rows={4}
                            {...register('descripcion')}
                            className="w-full rounded-md border border-slate-300 px-3 py-2 shadow-sm placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500 resize-y min-h-32"
                            placeholder="Describe tu evento: qué incluye, quién puede participar, qué deben traer los asistentes, etc."
                            maxLength={1000}
                            readOnly={visualizar}
                        />
                        {!visualizar && (
                            <p className="text-xs text-gray-500 mt-1">
                                Entre 10 y 1000 caracteres
                            </p>
                        )}
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
                            className="w-full rounded-md border border-slate-300 px-3 py-2 shadow-sm placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500 [color-scheme:light]"
                            min={new Date().toISOString().slice(0, 16)}
                            defaultValue={new Date(Date.now() + 24*60*60*1000).toISOString().slice(0, 16)}
                            disabled={visualizar}
                            step="60"
                        />
                        {!visualizar && (
                            <div className="mt-1 space-y-1">
                                <p className="text-xs text-gray-500">
                                    📅 Los eventos deben programarse para fechas futuras
                                </p>
                                <p className="text-xs text-blue-600">
                                    💡 Tip: Usa las flechas o escribe directamente la hora (ej: 14:30)
                                </p>
                            </div>
                        )}
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
                        <div className="space-y-2">
                            <input
                                type="number"
                                id="duracionMinutos"
                                {...register('duracionMinutos', {
                                    valueAsNumber: true,
                                })}
                                className="w-full rounded-md border border-slate-300 px-3 py-2 shadow-sm placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500"
                                placeholder="Ej: 120"
                                disabled={visualizar}
                            />
                            {!visualizar && (
                                <div className="flex gap-2 text-xs">
                                    <button
                                        type="button"
                                        onClick={() =>
                                            setValue('duracionMinutos', 60)
                                        }
                                        className="px-2 py-1 bg-gray-100 rounded hover:bg-gray-200"
                                    >
                                        1 hora
                                    </button>
                                    <button
                                        type="button"
                                        onClick={() =>
                                            setValue('duracionMinutos', 120)
                                        }
                                        className="px-2 py-1 bg-gray-100 rounded hover:bg-gray-200"
                                    >
                                        2 horas
                                    </button>
                                    <button
                                        type="button"
                                        onClick={() =>
                                            setValue('duracionMinutos', 180)
                                        }
                                        className="px-2 py-1 bg-gray-100 rounded hover:bg-gray-200"
                                    >
                                        3 horas
                                    </button>
                                    <button
                                        type="button"
                                        onClick={() =>
                                            setValue('duracionMinutos', 480)
                                        }
                                        className="px-2 py-1 bg-gray-100 rounded hover:bg-gray-200"
                                    >
                                        8 horas
                                    </button>
                                </div>
                            )}
                        </div>
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
                            placeholder="Ej: Auditorio Principal UTN, Av. Medrano 951, CABA"
                            disabled={visualizar}
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
                            placeholder="Ej: 50"
                            min="1"
                            disabled={visualizar}
                        />
                        {!visualizar && (
                            <p className="text-xs text-gray-500 mt-1">
                                Número máximo de personas que pueden inscribirse
                            </p>
                        )}
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
                            Precio (ARS)
                        </label>
                        <div className="space-y-2">
                            <div className="relative">
                                <span className="absolute left-3 top-2 text-gray-500">
                                    $
                                </span>
                                <input
                                    type="number"
                                    id="precio"
                                    {...register('precio', {
                                        valueAsNumber: true,
                                    })}
                                    className="w-full rounded-md border border-slate-300 pl-8 pr-3 py-2 shadow-sm placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500"
                                    placeholder="0.00"
                                    step="0.01"
                                    min="0"
                                    disabled={visualizar}
                                />
                            </div>
                            {!visualizar && (
                                <div className="flex gap-2 text-xs">
                                    <button
                                        type="button"
                                        onClick={() => setValue('precio', 0)}
                                        className="px-2 py-1 bg-green-100 text-green-800 rounded hover:bg-green-200"
                                    >
                                        Gratis
                                    </button>
                                    <button
                                        type="button"
                                        onClick={() => setValue('precio', 1000)}
                                        className="px-2 py-1 bg-gray-100 rounded hover:bg-gray-200"
                                    >
                                        $1,000
                                    </button>
                                    <button
                                        type="button"
                                        onClick={() => setValue('precio', 2500)}
                                        className="px-2 py-1 bg-gray-100 rounded hover:bg-gray-200"
                                    >
                                        $2,500
                                    </button>
                                    <button
                                        type="button"
                                        onClick={() => setValue('precio', 5000)}
                                        className="px-2 py-1 bg-gray-100 rounded hover:bg-gray-200"
                                    >
                                        $5,000
                                    </button>
                                </div>
                            )}
                        </div>
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
                        <select
                            id="categoria"
                            {...register('categoria')}
                            className="w-full rounded-md border border-slate-300 px-3 py-2 shadow-sm placeholder-slate-400 focus:outline-none focus:border-blue-500 focus:ring-2 focus:ring-blue-500"
                            disabled={visualizar}
                        >
                            <option value="">Seleccionar categoría</option>
                            {CATEGORIAS_EVENTO.map((categoria) => (
                                <option key={categoria} value={categoria}>
                                    {categoria}
                                </option>
                            ))}
                        </select>
                        {
                            <p
                                className="mt-1 text-sm text-red-600"
                                role="alert"
                            >
                                {errors.categoria?.message}
                            </p>
                        }
                    </div>

                    {!visualizar && (
                        <div className="mt-2 flex justify-end">
                            <button
                                type="submit"
                                className="rounded bg-blue-500 px-4 py-2 text-white hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-500"
                            >
                                Crear
                            </button>
                        </div>
                    )}
                </div>
            </form>
        </div>
    )
}
