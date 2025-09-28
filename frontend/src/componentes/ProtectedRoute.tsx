import React from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import { RolUsuario, tieneAlgunRol } from '../types/usuario'

interface ProtectedRouteProps {
    children: React.ReactNode
    rolesPermitidos?: RolUsuario[]
    redirectTo?: string
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({
    children,
    rolesPermitidos = [],
    redirectTo = '/login',
}) => {
    const { usuario, loading } = useAuth()
    const location = useLocation()

    // Mostrar loading mientras se verifica la autenticación
    if (loading) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="text-lg">Cargando...</div>
            </div>
        )
    }

    // Si no está autenticado, redirigir al login
    if (!usuario) {
        return <Navigate to="/login" state={{ from: location }} replace />
    }

    // Si se especificaron roles y el usuario no tiene ninguno de ellos
    if (
        rolesPermitidos.length > 0 &&
        !tieneAlgunRol(usuario, rolesPermitidos)
    ) {
        return (
            <div className="flex items-center justify-center min-h-screen">
                <div className="text-center">
                    <h2 className="text-xl font-semibold text-red-600 mb-2">
                        Acceso Denegado
                    </h2>
                    <p className="text-gray-600 mb-4">
                        No tienes permisos para acceder a esta página.
                    </p>
                    <button
                        onClick={() => window.history.back()}
                        className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
                    >
                        Volver
                    </button>
                </div>
            </div>
        )
    }

    return <>{children}</>
}

// Componente específico para rutas de admin
export const AdminRoute: React.FC<{ children: React.ReactNode }> = ({
    children,
}) => (
    <ProtectedRoute rolesPermitidos={[RolUsuario.ADMIN]}>
        {children}
    </ProtectedRoute>
)

// Componente específico para rutas de organizador
export const OrganizadorRoute: React.FC<{ children: React.ReactNode }> = ({
    children,
}) => (
    <ProtectedRoute
        rolesPermitidos={[RolUsuario.ORGANIZADOR, RolUsuario.ADMIN]}
    >
        {children}
    </ProtectedRoute>
)

// Componente para rutas que requieren cualquier usuario autenticado
export const AuthenticatedRoute: React.FC<{ children: React.ReactNode }> = ({
    children,
}) => <ProtectedRoute>{children}</ProtectedRoute>
