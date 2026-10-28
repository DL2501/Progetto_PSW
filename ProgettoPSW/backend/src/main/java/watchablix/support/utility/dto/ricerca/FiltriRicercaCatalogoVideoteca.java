package watchablix.support.utility.dto.ricerca;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import watchablix.entities.StatoVisione;
import watchablix.support.exceptions.InvalidSearchFiltersException;
import lombok.Getter;

@Getter
public class FiltriRicercaCatalogoVideoteca extends FiltriRicercaCatalogo {

    private StatoVisione statoVisione;


    @Jacksonized
    @Builder(builderMethodName = "ricercaVideotecaBuilder")
    public FiltriRicercaCatalogoVideoteca(String titolo, String genere, Integer annoUscita, Integer valutazione, StatoVisione statoVisione) throws InvalidSearchFiltersException {
        super(titolo, genere, annoUscita, valutazione);
        this.statoVisione = statoVisione;
    }






}
