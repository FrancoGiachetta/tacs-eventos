import { Navigate, Route, Routes, useLocation } from 'react-router-dom'
import BarraSup from './componentes/BarraSuperior'
import ListaEventos from './componentes/eventos/ListaEventos'
import Login from './componentes/Login'
import Registro from './componentes/Registro'
import Admin from './componentes/admin/Admin'
import MisInscripciones from './componentes/inscripciones/MisInscripciones'
import MisEventos from './componentes/eventos/MisEventos'
import GestionarEvento from './componentes/eventos/GestionarEvento'
import FormularioEvento from './componentes/eventos/FormularioEvento'

function App() {
    const location = useLocation()
    const isAuthPage =
        location.pathname === '/login' || location.pathname === '/registro'

    return (
        <>
            {!isAuthPage && <BarraSup />}
            <Routes>
                <Route path="/" element={<Navigate to="/login" replace />} />
                <Route path="/login" element={<Login />} />
                <Route path="/registro" element={<Registro />} />
                <Route path="/eventos" element={<ListaEventos />} />
                <Route
                    path="/mis-inscripciones"
                    element={<MisInscripciones />}
                />
                <Route
                    path="/organizador/eventos/nuevo"
                    element={<FormularioEvento />}
                />
                <Route
                    path="/organizador/mis-eventos"
                    element={<MisEventos />}
                />
                <Route path="/admin" element={<Admin />} />
                <Route
                    path="/organizador/eventos/:id"
                    element={<GestionarEvento />}
                />
                <Route path="*" element={<Navigate to="/eventos" replace />} />
            </Routes>
        </>
    )
}

export default App
