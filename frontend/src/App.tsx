import { Routes, Route, Navigate } from 'react-router-dom';
import BarraSup from './componentes/BarraSuperior';
import ListaEventos from './pantallas/eventos/ListaEventos';

function App() {

  return (
      <>
          <BarraSup />
          <Routes>
              <Route path="/" element={<ListaEventos/>} />
              <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
      </>
  )
}

export default App
