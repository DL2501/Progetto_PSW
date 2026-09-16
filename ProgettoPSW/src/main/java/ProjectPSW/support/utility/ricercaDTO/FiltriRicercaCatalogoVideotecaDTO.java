package ProjectPSW.support.utility.ricercaDTO;

import ProjectPSW.entities.StatoVisione;
import ProjectPSW.support.exceptions.InvalidSearchFiltersException;
import lombok.Getter;

@Getter
public class FiltriRicercaCatalogoVideotecaDTO extends FiltriRicercaCatalogoDTO {

    private StatoVisione statoVisione;


    public FiltriRicercaCatalogoVideotecaDTO(String titolo, String genere, Integer annoUscita, Integer valutazione, StatoVisione statoVisione) throws InvalidSearchFiltersException {
        super(titolo, genere, annoUscita, valutazione);
        this.statoVisione = statoVisione;
    }






}
