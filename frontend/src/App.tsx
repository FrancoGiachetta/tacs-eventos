import { Navigate, Route, Routes, useLocation } from 'react-router-dom'
import BarraSup from './componentes/BarraSuperior'
import ListaEventos from './componentes/ListaEventos'
import Login from './componentes/Login'
import Registro from './componentes/Registro'
import Admin from './componentes/admin/Admin'
import MisInscripciones from './componentes/inscripciones/MisInscripciones'
import MisEventos from './componentes/eventos/MisEventos'
import GestionarEvento from './componentes/eventos/GestionarEvento'
import FormularioEvento from './componentes/eventos/FormularioEvento'
import DetalleEvento from './componentes/eventos/DetalleEvento'
import { AuthProvider } from './contexts/AuthContext'
import { NotificationProvider } from './contexts/NotificationContext'
import ToastNotifications from './componentes/ToastNotifications'
import {
    ProtectedRoute,
    AdminRoute,
    OrganizadorRoute,
} from './componentes/ProtectedRoute'

function App() {
    return (
        <AuthProvider>
            <NotificationProvider>
                <AppContent />
            </NotificationProvider>
        </AuthProvider>
    )
}

function AppContent() {
    const location = useLocation()
    const isAuthPage =
        location.pathname === '/login' || location.pathname === '/registro'

    return (
        <>
            {!isAuthPage && <BarraSup />}
            <ToastNotifications />
            <Routes>
                <Route path="/" element={<Navigate to="/login" replace />} />
                <Route path="/login" element={<Login />} />
                <Route path="/registro" element={<Registro />} />
                <Route
                    path="/eventos"
                    element={
                        <ProtectedRoute>
                            <ListaEventos />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/eventos/:eventoId"
                    element={
                        <ProtectedRoute>
                            <DetalleEvento />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/mis-inscripciones"
                    element={
                        <ProtectedRoute>
                            <MisInscripciones />
                        </ProtectedRoute>
                    }
                />
                <Route
                    path="/organizador/eventos/nuevo"
                    element={
                        <OrganizadorRoute>
                            <FormularioEvento />
                        </OrganizadorRoute>
                    }
                />
                <Route
                    path="/organizador/mis-eventos"
                    element={
                        <OrganizadorRoute>
                            <MisEventos />
                        </OrganizadorRoute>
                    }
                />
                <Route
                    path="/organizador/eventos/:id"
                    element={
                        <OrganizadorRoute>
                            <GestionarEventoConId />
                        </OrganizadorRoute>
                    }
                />
                <Route
                    path="/admin"
                    element={
                        <AdminRoute>
                            <Admin />
                        </AdminRoute>
                    }
                />
                <Route path="*" element={<Navigate to="/eventos" replace />} />
            </Routes>
        </>
    )
}

function GestionarEventoConId() {
    return <GestionarEvento />
}

export default App
