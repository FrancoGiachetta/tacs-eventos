import type { Evento } from '../types/evento';
interface EventCardProps {
    evento: Evento;
    onVerDetalle: (eventoId: string) => void;
}
export default function EventCard({ evento, onVerDetalle }: EventCardProps): import("react/jsx-runtime").JSX.Element;
export {};
