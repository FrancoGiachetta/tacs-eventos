import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import EventFilters from '../componentes/EventFilters';
import EventList from '../componentes/EventList';
import Pagination from '../componentes/Pagination';
import type { Evento, EventoFiltros } from '../types/evento';
import api from '../lib/api';
import { useQuery } from '@tanstack/react-query';

const ITEMS_PER_PAGE = 9;

export default function ListaEventos() {
  const [filtros, setFiltros] = useState<EventoFiltros>({});
  const [pagina, setPagina] = useState(1);
  const navigate = useNavigate();

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['eventos', filtros, pagina],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (filtros.query?.trim()) params.append('query', filtros.query.trim());
      if (filtros.precioPesosMin !== undefined) params.append('precioPesosMin', filtros.precioPesosMin.toString());
      if (filtros.precioPesosMax !== undefined) params.append('precioPesosMax', filtros.precioPesosMax.toString());
      if (filtros.fechaInicioMin) params.append('fechaInicioMin', filtros.fechaInicioMin);
      if (filtros.fechaInicioMax) params.append('fechaInicioMax', filtros.fechaInicioMax);
      if (filtros.categoria && filtros.categoria !== '') params.append('categoria', filtros.categoria);
      params.append('page', pagina.toString());

      const res = await api.get<Evento[]>('/api/v1/evento?' + params.toString());
      return res.data;
    }
  });

  const handleFilterChange = (nuevosFiltros: EventoFiltros) => {
    setFiltros(nuevosFiltros);
    setPagina(1);
  };

  const handleReset = () => {
    setFiltros({});
    setPagina(1);
  };

  const handleVerDetalle = (eventoId: string) => {
    navigate(`/eventos/${eventoId}`);
  };

  const totalPages = data ? Math.ceil(data.length / ITEMS_PER_PAGE) : 0;

  return (
    <div className="container mx-auto px-4 py-8">
      <h1 className="text-2xl font-bold mb-6">Descubrir Eventos</h1>

      <EventFilters
        onFilterChange={handleFilterChange}
        onReset={handleReset}
      />

      <EventList
        eventos={data || []}
        isLoading={isLoading}
        error={error?.message}
        onRetry={() => refetch()}
        onVerDetalle={handleVerDetalle}
      />

      <Pagination
        currentPage={pagina}
        totalPages={totalPages}
        onPageChange={setPagina}
      />
    </div>
  );
}
