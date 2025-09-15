import { Routes, Route, Navigate } from 'react-router-dom'
import BarraSup from './componentes/BarraSuperior'
import ListaEventos from './componentes/eventos/ListaEventos'
import Login from './componentes/Login'
import Registro from './componentes/Registro'

import { useLocation } from 'react-router-dom'
import MisEventos from './componentes/eventos/MisEventos'

function App() {
    const location = useLocation();
    const isAuthPage = location.pathname === '/login' || location.pathname === '/registro';
    
    return (
        <>
            {!isAuthPage && <BarraSup />}
            <Routes>
                <Route path="/" element={<Navigate to="/login" replace />} />
                <Route path="/login" element={<Login />} />
                <Route path="/registro" element={<Registro />} />
                <Route path="/eventos" element={<ListaEventos />} />
                <Route
                    path="/organizador/mis-eventos"
                    element={<MisEventos />}
                />
                <Route path="*" element={<Navigate to="/login" replace />} />
            </Routes>
        </>
    )
}

export default App
