package tacs.eventos.repository.evento.busqueda;

import lombok.AllArgsConstructor;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import tacs.eventos.model.Evento;
import tacs.eventos.repository.FiltroBusqueda;

import java.util.List;

@AllArgsConstructor
public class FiltradoPorPalabrasClave implements FiltroBusqueda<Evento> {
    private List<String> palabrasClave;

    @Override
    public Boolean aplicarCondicionfiltrado(Evento evento) {
        if (palabrasClave == null || palabrasClave.isEmpty()) {
            return true;
        }

        String eventoTexto = (evento.getTitulo() + " " + evento.getDescripcion()).toLowerCase();

        // Todas las palabras clave deben estar presentes
        return palabrasClave.stream().map(String::toLowerCase).allMatch(palabra -> eventoTexto.contains(palabra));
    }
}
