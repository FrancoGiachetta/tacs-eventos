package tacs.eventos.repository;

public interface FiltroBusqueda<T> {
    Boolean aplicarCondicionfiltrado(T elemento);
}
