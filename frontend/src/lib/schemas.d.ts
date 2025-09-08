import { z } from 'zod';
export declare const SchemaCrearEvento: z.ZodObject<{
    titulo: z.ZodString;
    descripcion: z.ZodString;
    fechaHoraInicio: z.ZodString;
    duracionMinutos: z.ZodNumber;
    ubicacion: z.ZodString;
    cupoMaximo: z.ZodNumber;
    precio: z.ZodNumber;
    categoria: z.ZodNullable<z.ZodOptional<z.ZodString>>;
    abierto: z.ZodBoolean;
}, z.core.$strip>;
export type InputCrearEvento = z.infer<typeof SchemaCrearEvento>;
