
import { Routes, Route, Navigate } from 'react-router-dom';
import BarraSup from './componentes/BarraSuperior';
import ListaEventos from './pantallas/ListaEventos';
import Login from './pantallas/Login';
import Registro from './pantallas/Registro';
import PrivateRoute from './componentes/PrivateRoute';
import { useLocation } from 'react-router-dom';
import { useAuth } from './hooks/useAuth';

function App() {
    const location = useLocation();
    const { isAuthenticated } = useAuth();
    const isAuthPage = location.pathname === '/login' || location.pathname === '/registro';
    
    return (
        <>
            {!isAuthPage && <BarraSup />}
            <Routes>
                <Route path="/" element={
                    isAuthenticated ? 
                        <Navigate to="/eventos" replace /> : 
                        <Navigate to="/login" replace state={{ from: location }} />
                } />
                <Route path="/login" element={
                    isAuthenticated ? 
                        <Navigate to="/eventos" replace /> : 
                        <Login />
                } />
                <Route path="/registro" element={
                    isAuthenticated ? 
                        <Navigate to="/eventos" replace /> : 
                        <Registro />
                } />
                <Route path="/eventos" element={
                    <PrivateRoute>
                        <ListaEventos />
                    </PrivateRoute>
                } />
                <Route path="/organizador/new" element={
                    <PrivateRoute>
                        <div>Crear Evento</div>
                    </PrivateRoute>
                } />
                <Route path="/mis-inscripciones" element={
                    <PrivateRoute>
                        <div>Mis Inscripciones</div>
                    </PrivateRoute>
                } />
                <Route path="/admin" element={
                    <PrivateRoute>
                        <div>Admin</div>
                    </PrivateRoute>
                } />
                <Route path="*" element={
                    isAuthenticated ? 
                        <Navigate to="/eventos" replace /> : 
                        <Navigate to="/login" replace state={{ from: location }} />
                } />
            </Routes>
        </>
    );
}

export default App;
