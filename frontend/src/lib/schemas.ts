import { z } from 'zod'
import { CATEGORIAS_EVENTO, type CategoriaEvento } from '../types/categorias'

function mensajeDeErrorMinimoCaracteres(cantidadMinima: number) {
    return `Ingrese al menos ${cantidadMinima} caracteres`
}
function mensajeDeErrorMaximoCaracteres(cantidadMaxima: number) {
    return `No puede exceder los ${cantidadMaxima} caracteres`
}

let mensajeDeErrorNumeroNoEntero = 'Ingrese un número entero'
let mensajeDeErrorNumeroNegativo = 'El valor no puede ser negativo'
let mensajeDeErrorNumeroInvalido = 'Ingrese un número'

// Schema para la creacion de un evento, aseguro que tiene el formato correcto
export const SchemaCrearEvento = z.object({
    titulo: z
        .string()
        .min(3, mensajeDeErrorMinimoCaracteres(3))
        .max(100, mensajeDeErrorMaximoCaracteres(100)),
    descripcion: z
        .string()
        .min(10, mensajeDeErrorMinimoCaracteres(10))
        .max(1000, mensajeDeErrorMaximoCaracteres(1000)),
    fechaHoraInicio: z.string().datetime({
        local: true,
        message: 'Ingrese una fecha válida',
    }),
    duracionMinutos: z
        .number(mensajeDeErrorNumeroInvalido)
        .int(mensajeDeErrorNumeroNoEntero)
        .positive(mensajeDeErrorNumeroNegativo),
    ubicacion: z
        .string()
        .min(3, mensajeDeErrorMinimoCaracteres(3))
        .max(300, mensajeDeErrorMaximoCaracteres(300)),
    cupoMaximo: z
        .number(mensajeDeErrorNumeroInvalido)
        .int(mensajeDeErrorNumeroNoEntero)
        .positive(mensajeDeErrorNumeroNegativo),
    precio: z.number(mensajeDeErrorNumeroInvalido).min(0),
    categoria: z
        .enum(CATEGORIAS_EVENTO, {
            message: 'Selecciona una categoría válida',
        })
        .optional()
        .nullable(),
})
export type InputCrearEvento = z.infer<typeof SchemaCrearEvento>

//todo hacer lo mismo para los otros objetos
