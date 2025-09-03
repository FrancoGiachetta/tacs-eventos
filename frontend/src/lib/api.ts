import axios from 'axios';


const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080',
    withCredentials: false
});


// Interceptor opcional para logging o auth fake
api.interceptors.response.use(
    (r) => r,
    (e) => {
        console.error('API error:', e?.response?.status, e?.response?.data);
        return Promise.reject(e);
    }
);


export default api;