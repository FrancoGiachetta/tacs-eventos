import React, { useState } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { useAuth } from '../contexts/AuthContext'
import PasswordInput from './PasswordInput'

const Login: React.FC = () => {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [error, setError] = useState('')
    const [loading, setLoading] = useState(false)
    const navigate = useNavigate()
    const { login } = useAuth()

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setError('')
        setLoading(true)

        try {
            await login(email, password)
            // Redirigir a la pantalla principal
            navigate('/eventos')
        } catch (err: any) {
            setError('Credenciales incorrectas o error de conexión')
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <form
                onSubmit={handleSubmit}
                className="bg-white p-8 rounded shadow-md w-80"
            >
                <h2 className="text-2xl font-bold mb-6 text-center">
                    Iniciar Sesión
                </h2>
                {error && (
                    <div className="text-red-600 mb-2 text-center">{error}</div>
                )}
                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    className="w-full p-2 mb-4 border rounded"
                    required
                />
                <div className="mb-4">
                    <PasswordInput
                        placeholder="Contraseña"
                        value={password}
                        onChange={setPassword}
                        required
                    />
                </div>
                <button
                    type="submit"
                    disabled={loading}
                    className="w-full bg-blue-600 text-white p-2 rounded hover:bg-blue-700 mb-2 disabled:opacity-50 disabled:cursor-not-allowed"
                >
                    {loading ? 'Iniciando sesión...' : 'Entrar'}
                </button>
                <div className="text-center mt-2">
                    <span>¿No tenés cuenta? </span>
                    <Link
                        to="/registro"
                        className="text-blue-600 hover:underline"
                    >
                        Registrate
                    </Link>
                </div>
            </form>
        </div>
    )
}

export default Login
