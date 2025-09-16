import axios, { InternalAxiosRequestConfig } from 'axios'

const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
    withCredentials: false,
})

function logoutAndRedirect(reason = 'Sesión inválida') {
    localStorage.removeItem('token')
    localStorage.removeItem('authExpiresAt')
    window.location.href = '/login'
    console.warn('Logged out:', reason)
    return Promise.reject(new Error(reason))
}

const NOT_LOGGED_PATHS = ['/login', '/register', 'logout']

api.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
        if (NOT_LOGGED_PATHS.includes(window.location.pathname)) {
            return config
        }

        const token = localStorage.getItem('token')
        const expiresAt = localStorage.getItem('authExpiresAt')

        if (!token || !expiresAt) {
            return logoutAndRedirect('Sesión inválida')
        }

        const now = new Date()
        const exp = new Date(expiresAt)

        if (now >= exp) {
            return logoutAndRedirect('Token expirado')
        }

        // Si todavía es válido, lo agregamos
        config.headers.Authorization = `Bearer ${token}`

        return config
    },
    (error) => {
        return Promise.reject(error)
    }
)

api.interceptors.response.use(
    (r) => r,
    (e) => {
        const status = e?.response?.status

        if (status === 401) {
            return logoutAndRedirect('No autorizado')
        }

        console.error('API error:', e?.response?.status, e?.response?.data)
        return Promise.reject(e)
    }
)

export default api
