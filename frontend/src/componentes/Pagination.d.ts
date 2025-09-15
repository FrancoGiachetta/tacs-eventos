interface PaginationProps {
    currentPage: number
    totalPages: number
    onPageChange: (page: number) => void
}
export default function Pagination({
    currentPage,
    totalPages,
    onPageChange,
}: PaginationProps): import('react/jsx-runtime').JSX.Element | null
export {}
