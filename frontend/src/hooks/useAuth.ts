export function useAuth() {
    const token = localStorage.getItem('token');

    return {
        isAuthenticated: Boolean(token),
        token
    };
}