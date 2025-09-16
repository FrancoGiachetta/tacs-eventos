import { ToastContainer, type ToastContainerProps } from 'react-toastify'

export default function ContainerDeToast(props: Partial<ToastContainerProps>) {
    return (
        <ToastContainer
            position="bottom-center"
            autoClose={5000}
            hideProgressBar
            pauseOnHover
            closeButton={false}
            {...props}
        />
    )
}
