import type { Evento } from '../types/evento';
interface EventListProps {
    eventos: Evento[];
    isLoading: boolean;
    error?: string;
    onRetry: () => void;
    onVerDetalle: (eventoId: string) => void;
}
export default function EventList({ eventos, isLoading, error, onRetry, onVerDetalle }: EventListProps): import("react/jsx-runtime").JSX.Element;
export {};
