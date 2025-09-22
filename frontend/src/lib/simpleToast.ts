// Simple toast replacement to avoid react-toastify hook issues
export const toast = {
    success: (message: string) => {
        console.log('SUCCESS:', message)
        alert(`✅ ${message}`)
    },
    error: (message: string) => {
        console.error('ERROR:', message)
        alert(`❌ ${message}`)
    },
    info: (message: string) => {
        console.log('INFO:', message)
        alert(`ℹ️ ${message}`)
    },
    warning: (message: string) => {
        console.warn('WARNING:', message)
        alert(`⚠️ ${message}`)
    },
}
