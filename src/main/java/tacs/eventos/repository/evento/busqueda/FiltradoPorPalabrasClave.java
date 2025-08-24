package tacs.eventos.repository.evento.busqueda;

import lombok.AllArgsConstructor;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import tacs.eventos.model.Evento;
import tacs.eventos.repository.FiltroBusqueda;

import java.util.List;

@AllArgsConstructor
public class FiltradoPorPalabrasClave implements FiltroBusqueda<Evento> {
    private final int ratioMinimo = 70;
    private List<String> palabrasClave;

    @Override
    public Boolean aplicarCondicionfiltrado(Evento evento) {
        String palabrasClaveString = String.join(" ", palabrasClave);
        double ratio = FuzzySearch.tokenSetRatio(evento.getDescripcion(), palabrasClaveString);

        return ratio >= ratioMinimo;
    }
}
