package ProjectPSW.support.Utility.ricercaDTO;

import ProjectPSW.entities.StatoVisione;
import ProjectPSW.support.Exceptions.InvalidSearchFiltersException;
import lombok.Getter;

@Getter
public class FiltriRicercaCatalogoVideotecaDTO extends FiltriRicercaCatalogoDTO {

    private StatoVisione statoVisione;


    public FiltriRicercaCatalogoVideotecaDTO(String titolo, String genere, Integer annoUscita, Integer valutazione, StatoVisione statoVisione) throws InvalidSearchFiltersException {
        super(titolo, genere, annoUscita, valutazione);
        this.statoVisione = statoVisione;
    }






}
