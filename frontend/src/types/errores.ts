export interface ErrorDelServidor {
    errores: Error[]
}

export interface Error {
    campo: string
    mensaje: string
}

export function esError(obj: any): obj is Error {
    return 'campo' in obj && 'mensaje' in obj
}

export function esErrorDelServidor(obj: any): obj is ErrorDelServidor {
    return (
        'errores' in obj &&
        Array.isArray(obj.errores) &&
        obj.errores.every(esError)
    )
}
