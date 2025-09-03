export interface Evento {
    id: string;
    titulo: string;
    descripcion: string;
    fechaHoraInicio: string;
    duracionMinutos: number;
    ubicacion: string;
    cupoMaximo: number;
    precio: number;
    categoria?: string | null;
    abierto: boolean;
}


export interface Inscripcion {
    id: string;
    estado: 'CONFITMADA' | 'WAITLIST' | 'CANCELADA';
    createdAt: string;
}


export interface Estadisticas {

}


export interface Usuario {
    mail: string;
    role: 'usuario' | 'admin';
}