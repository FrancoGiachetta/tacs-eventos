import React, { useState, useEffect } from 'react'
import api from '../../lib/api'
import { RolUsuario } from '../../types/usuario'
import { toast } from '../../lib/simpleToast'

interface Usuario {
    id: string
    email: string
    rol: RolUsuario
    fechaCreacion: string
}

const GestionUsuarios: React.FC = () => {
    const [usuarios, setUsuarios] = useState<Usuario[]>([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState('')

    useEffect(() => {
        cargarUsuarios()
    }, [])

    const cargarUsuarios = async () => {
        try {
            setLoading(true)
            const token = localStorage.getItem('authToken')
            const response = await api.get('/api/v1/admin/usuarios', {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            })
            setUsuarios(response.data)
            setError('')
        } catch (err) {
            setError('Error al cargar usuarios')
            console.error('Error:', err)
        } finally {
            setLoading(false)
        }
    }

    const cambiarRol = async (usuarioId: string, nuevoRol: RolUsuario) => {
        try {
            const token = localStorage.getItem('authToken')
            await api.put(
                `/api/v1/admin/usuarios/${usuarioId}/rol`,
                {
                    nuevoRol: nuevoRol,
                },
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                }
            )

            // Actualizar la lista local
            setUsuarios(
                usuarios.map((usuario) =>
                    usuario.id === usuarioId
                        ? { ...usuario, rol: nuevoRol }
                        : usuario
                )
            )

            toast.success(`Rol cambiado a ${nuevoRol} exitosamente`)
        } catch (err) {
            toast.error('Error al cambiar rol')
            console.error('Error:', err)
        }
    }

    const getRolColor = (rol: RolUsuario) => {
        switch (rol) {
            case RolUsuario.ADMIN:
                return 'bg-red-100 text-red-800'
            case RolUsuario.ORGANIZADOR:
                return 'bg-blue-100 text-blue-800'
            case RolUsuario.USUARIO:
                return 'bg-green-100 text-green-800'
            default:
                return 'bg-gray-100 text-gray-800'
        }
    }

    const formatearFecha = (fecha: string) => {
        return new Date(fecha).toLocaleDateString('es-AR', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
        })
    }

    if (loading) {
        return (
            <div className="flex justify-center items-center h-64">
                <div className="text-gray-600">Cargando usuarios...</div>
            </div>
        )
    }

    return (
        <div className="bg-white rounded-lg shadow p-6">
            <div className="flex justify-between items-center mb-6">
                <h2 className="text-2xl font-bold text-gray-900">
                    Gestión de Usuarios
                </h2>
                <button
                    onClick={cargarUsuarios}
                    className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
                >
                    Actualizar
                </button>
            </div>

            {error && (
                <div className="bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded mb-4">
                    {error}
                </div>
            )}

            <div className="overflow-x-auto">
                <table className="min-w-full table-auto">
                    <thead>
                        <tr className="bg-gray-50">
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Email
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Rol Actual
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Fecha Registro
                            </th>
                            <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">
                                Cambiar Rol
                            </th>
                        </tr>
                    </thead>
                    <tbody className="bg-white divide-y divide-gray-200">
                        {usuarios.map((usuario) => (
                            <tr key={usuario.id} className="hover:bg-gray-50">
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <div className="text-sm font-medium text-gray-900">
                                        {usuario.email}
                                    </div>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <span
                                        className={`inline-flex px-2 py-1 text-xs font-semibold rounded-full ${getRolColor(usuario.rol)}`}
                                    >
                                        {usuario.rol}
                                    </span>
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                                    {formatearFecha(usuario.fechaCreacion)}
                                </td>
                                <td className="px-6 py-4 whitespace-nowrap">
                                    <select
                                        value={usuario.rol}
                                        onChange={(e) =>
                                            cambiarRol(
                                                usuario.id,
                                                e.target.value as RolUsuario
                                            )
                                        }
                                        className="border border-gray-300 rounded px-3 py-1 text-sm"
                                        disabled={
                                            usuario.rol === RolUsuario.ADMIN
                                        }
                                    >
                                        <option value={RolUsuario.USUARIO}>
                                            Usuario
                                        </option>
                                        <option value={RolUsuario.ORGANIZADOR}>
                                            Organizador
                                        </option>
                                        <option value={RolUsuario.ADMIN}>
                                            Admin
                                        </option>
                                    </select>
                                    {usuario.rol === RolUsuario.ADMIN && (
                                        <div className="text-xs text-gray-500 mt-1">
                                            No modificable
                                        </div>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>

            {usuarios.length === 0 && !loading && (
                <div className="text-center py-8 text-gray-500">
                    No hay usuarios registrados
                </div>
            )}
        </div>
    )
}

export default GestionUsuarios
