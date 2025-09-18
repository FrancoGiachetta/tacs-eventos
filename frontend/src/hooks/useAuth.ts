import { useState, useEffect } from 'react'

export function useAuth() {
    const [isAuthenticated, setIsAuthenticated] = useState(false)

    useEffect(() => {
        const token = localStorage.getItem('token')
        const expiresAt = localStorage.getItem('authExpiresAt')

        if (token && expiresAt) {
            const now = new Date()
            const expiration = new Date(expiresAt)
            setIsAuthenticated(now < expiration)
        } else {
            setIsAuthenticated(false)
        }
    }, [])

    return {
        isAuthenticated,
        token: localStorage.getItem('token'),
    }
}
