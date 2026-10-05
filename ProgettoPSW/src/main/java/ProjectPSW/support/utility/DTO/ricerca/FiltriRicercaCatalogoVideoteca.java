package ProjectPSW.support.utility.DTO.ricerca;

import ProjectPSW.entities.StatoVisione;
import ProjectPSW.support.exceptions.InvalidSearchFiltersException;
import lombok.Getter;

@Getter
public class FiltriRicercaCatalogoVideoteca extends FiltriRicercaCatalogo {

    private StatoVisione statoVisione;


    public FiltriRicercaCatalogoVideoteca(String titolo, String genere, Integer annoUscita, Integer valutazione, StatoVisione statoVisione) throws InvalidSearchFiltersException {
        super(titolo, genere, annoUscita, valutazione);
        this.statoVisione = statoVisione;
    }






}
