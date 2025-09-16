import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'

interface PrivateRouteProps {
    children: React.ReactNode
}

export default function PrivateRoute({ children }: PrivateRouteProps) {
    const { isAuthenticated } = useAuth()
    const location = useLocation()

    if (!isAuthenticated) {
        // Redirige al login pero guarda la ubicación actual
        return <Navigate to="/login" replace state={{ from: location }} />
    }

    return <>{children}</>
}
