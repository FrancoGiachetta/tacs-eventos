import React, { useState } from 'react'
import api from '../lib/api'
import { useNavigate, Link } from 'react-router-dom'
import PasswordInput from './PasswordInput'

const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const passwordRegex = /^(?=.*[A-Za-z])(?=.*\d).{8,72}$/

const Registro: React.FC = () => {
    const [email, setEmail] = useState('')
    const [password, setPassword] = useState('')
    const [confirmPassword, setConfirmPassword] = useState('')
    const [error, setError] = useState('')
    const [success, setSuccess] = useState('')
    const navigate = useNavigate()

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault()
        setError('')
        setSuccess('')
        if (!emailRegex.test(email)) {
            setError('El email no es válido')
            return
        }
        if (!passwordRegex.test(password)) {
            setError(
                'La contraseña debe tener al menos 8 caracteres, incluir letras y números'
            )
            return
        }
        if (password !== confirmPassword) {
            setError('Las contraseñas no coinciden')
            return
        }
        try {
            await api.post('/api/v1/auth/register', { email, password })
            setSuccess('Registro exitoso. Redirigiendo al login...')
            setTimeout(() => navigate('/login'), 1500)
        } catch (err: any) {
            setError('Error al registrar usuario o email ya registrado')
        }
    }

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100">
            <form
                onSubmit={handleSubmit}
                className="bg-white p-8 rounded shadow-md w-80"
            >
                <h2 className="text-2xl font-bold mb-6 text-center">
                    Registro
                </h2>
                {error && (
                    <div className="text-red-600 mb-2 text-center">{error}</div>
                )}
                {success && (
                    <div className="text-green-600 mb-2 text-center">
                        {success}
                    </div>
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
                <div className="mb-4">
                    <PasswordInput
                        placeholder="Confirmar Contraseña"
                        value={confirmPassword}
                        onChange={setConfirmPassword}
                        required
                    />
                </div>
                <button
                    type="submit"
                    className="w-full bg-green-600 text-white p-2 rounded hover:bg-green-700"
                >
                    Registrarse
                </button>
                <div className="text-center mt-2">
                    <span>¿Ya tenés cuenta? </span>
                    <Link to="/login" className="text-blue-600 hover:underline">
                        Iniciar sesión
                    </Link>
                </div>
            </form>
        </div>
    )
}

export default Registro
