import React, {
    createContext,
    useContext,
    useState,
    useEffect,
    ReactNode,
} from 'react'
import api from '../lib/api'
import { UsuarioAutenticado, RolUsuario } from '../types/usuario'

interface AuthContextType {
    usuario: UsuarioAutenticado | null
    loading: boolean
    login: (email: string, password: string) => Promise<void>
    logout: () => Promise<void>
    isAuthenticated: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export const useAuth = (): AuthContextType => {
    const context = useContext(AuthContext)
    if (!context) {
        throw new Error('useAuth debe usarse dentro de un AuthProvider')
    }
    return context
}

interface AuthProviderProps {
    children: ReactNode
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
    const [usuario, setUsuario] = useState<UsuarioAutenticado | null>(null)
    const [loading, setLoading] = useState(true)

    // Verificar si hay un token válido al cargar la app
    useEffect(() => {
        const verificarToken = async () => {
            const token = localStorage.getItem('token')
            const expiresAt = localStorage.getItem('authExpiresAt')

            if (!token || !expiresAt) {
                setLoading(false)
                return
            }

            // Verificar si el token no ha expirado
            if (new Date() > new Date(expiresAt)) {
                localStorage.removeItem('token')
                localStorage.removeItem('authExpiresAt')
                setLoading(false)
                return
            }

            try {
                // Obtener información del usuario actual
                const response = await api.get('/api/v1/usuario/me')
                const userData = response.data

                setUsuario({
                    id: userData.id,
                    email: userData.email,
                    roles: userData.rol ? [userData.rol] : [RolUsuario.USUARIO], // Convertir rol singular a array
                })
            } catch (error) {
                console.error('Error verificando token:', error)
                localStorage.removeItem('token')
                localStorage.removeItem('authExpiresAt')
            } finally {
                setLoading(false)
            }
        }

        verificarToken()
    }, [])

    const login = async (email: string, password: string): Promise<void> => {
        try {
            const response = await api.post('/api/v1/auth/login', {
                email,
                password,
            })

            const { token, expiresAt } = response.data

            // Guardar token
            localStorage.setItem('token', token)
            localStorage.setItem('authExpiresAt', expiresAt)

            // Obtener información del usuario después del login
            // Enviamos manualmente el token ya que estamos en la ruta /login
            const userResponse = await api.get('/api/v1/usuario/me', {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
            const userData = userResponse.data

            // Establecer usuario
            setUsuario({
                id: userData.id,
                email: userData.email,
                roles: userData.rol ? [userData.rol] : [RolUsuario.USUARIO],
            })
        } catch (error) {
            throw error
        }
    }

    const logout = async (): Promise<void> => {
        try {
            const token = localStorage.getItem('token')
            if (token) {
                await api.post(
                    '/api/v1/auth/logout',
                    {},
                    {
                        headers: {
                            'X-Session-Token': token,
                        },
                    }
                )
            }
        } catch (error) {
            console.error('Error durante logout:', error)
        } finally {
            localStorage.removeItem('token')
            localStorage.removeItem('authExpiresAt')
            setUsuario(null)
        }
    }

    const value: AuthContextType = {
        usuario,
        loading,
        login,
        logout,
        isAuthenticated: !!usuario,
    }

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}
