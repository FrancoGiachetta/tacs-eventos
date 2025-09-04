import axios from 'axios';


const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
    withCredentials: false
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem('token');
    const expiresAt = localStorage.getItem("authExpiresAt");


    if (token && expiresAt) {
        const now = new Date();
        const exp = new Date(expiresAt);

        if (now >= exp) {
            // Token vencido -> limpiar y redirigir al login
            localStorage.removeItem("token");
            localStorage.removeItem("authExpiresAt");
            window.location.href = "/login";
            return Promise.reject(new Error("Token expired"));
        }

        // Si todavía es válido, lo agregamos
        config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
}, (error) => {
    return Promise.reject(error);
});

// Interceptor opcional para logging o auth fake
api.interceptors.response.use(
    (r) => r,
    (e) => {
        console.error('API error:', e?.response?.status, e?.response?.data);
        return Promise.reject(e);
    }
    //todo: agregar algun retorno del
);


export default api;