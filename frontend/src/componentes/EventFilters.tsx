import { useState } from 'react';
import type { EventoFiltros } from '../types/evento';

interface EventFiltersProps {
  onFilterChange: (filtros: EventoFiltros) => void;
  onReset: () => void;
}

export default function EventFilters({ onFilterChange, onReset }: EventFiltersProps) {
  const [filtros, setFiltros] = useState<EventoFiltros>({});
  const [esGratis, setEsGratis] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    onFilterChange(filtros);
  };

  const handleReset = () => {
    setFiltros({});
    setEsGratis(false);
    onReset();
  };

  return (
    <form onSubmit={handleSubmit} className='bg-white p-4 rounded-lg shadow mb-4'>
      <div className='grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4'>
        <div>
          <input
            type='text'
            placeholder='Buscar eventos...'
            className='w-full p-2 border rounded'
            value={filtros.query || ''}
            onChange={e => setFiltros(prev => ({ ...prev, query: e.target.value }))}
          />
        </div>

        <div className='flex gap-2'>
          <input
            type='date'
            className='w-full p-2 border rounded'
            value={filtros.fechaInicioMin || ''}
            onChange={e => setFiltros(prev => ({ ...prev, fechaInicioMin: e.target.value }))}
          />
          <input
            type='date'
            className='w-full p-2 border rounded'
            value={filtros.fechaInicioMax || ''}
            onChange={e => setFiltros(prev => ({ ...prev, fechaInicioMax: e.target.value }))}
          />
        </div>

        <div>
          <select
            className='w-full p-2 border rounded'
            value={filtros.categoria || ''}
            onChange={e => {
              const value = e.target.value;
              setFiltros(prev => ({
                ...prev,
                categoria: value === '' ? undefined : value
              }));
            }}
          >
            <option value=''>Todas las categorías</option>
            <option value='Música'>Música</option>
            <option value='Deportes'>Deportes</option>
            <option value='Teatro'>Teatro</option>
            <option value='Arte'>Arte</option>
            <option value='Gastronomía'>Gastronomía</option>
          </select>
        </div>

        <div className='flex items-center gap-2'>
          <input
            type='range'
            min='0'
            max='10000'
            step='100'
            className='w-full'
            value={filtros.precioPesosMax || 10000}
            onChange={e => setFiltros(prev => ({ ...prev, precioPesosMax: Number(e.target.value) }))}
            disabled={esGratis}
          />
          <span className='whitespace-nowrap'>
            ${filtros.precioPesosMax !== undefined ? filtros.precioPesosMax : 10000}
          </span>
        </div>

        <div className='flex items-center gap-2'>
          <input
            type='checkbox'
            id='gratis'
            checked={esGratis}
            onChange={e => {
              setEsGratis(e.target.checked);
              if (e.target.checked) {
                setFiltros(prev => ({ ...prev, precioPesosMax: 0 }));
              }
            }}
          />
          <label htmlFor='gratis'>Solo eventos gratis</label>
        </div>
      </div>

      <div className='flex justify-end gap-2 mt-4'>
        <button
          type='button'
          onClick={handleReset}
          className='px-4 py-2 text-gray-600 border rounded hover:bg-gray-100'
        >
          Limpiar
        </button>
        <button
          type='submit'
          className='px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700'
        >
          Aplicar filtros
        </button>
      </div>
    </form>
  );
}
