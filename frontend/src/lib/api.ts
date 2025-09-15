import axios, { InternalAxiosRequestConfig } from 'axios'

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
    withCredentials: false,
})

api.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        const token = localStorage.getItem('token')
        const expiresAt = localStorage.getItem('authExpiresAt')

        if (token && expiresAt) {
            const now = new Date()
            const exp = new Date(expiresAt)

            if (now >= exp) {
                localStorage.removeItem('token')
                localStorage.removeItem('authExpiresAt')
                window.location.href = '/login'
                return Promise.reject(new Error('Sesión expirada'))
            }

            if (config.headers) {
                config.headers.Authorization = `Bearer ${token}`
            }
        }
        return config
    },
    (error: unknown) => Promise.reject(error)
)

export default api
