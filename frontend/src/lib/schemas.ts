import { z } from "zod";

// Schema para la creacion de un evento, aseguro que tiene el formato correcto
export const SchemaCrearEvento = z.object({
  titulo: z.string().min(3),
  descripcion: z.string().min(10),
  fechaHoraInicio: z.string(),
  duracionMinutos: z.number().int().positive(),
  ubicacion: z.string().min(3),
  cupoMaximo: z.number().int().positive(),
  precio: z.number().min(0),
  categoria: z.string().optional().nullable(),
  abierto: z.boolean(),
});
export type InputCrearEvento = z.infer<typeof SchemaCrearEvento>;

//todo hacer lo mismo para los otros objetos
