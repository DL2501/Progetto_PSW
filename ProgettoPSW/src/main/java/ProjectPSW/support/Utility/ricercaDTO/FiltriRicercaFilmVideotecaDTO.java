package ProjectPSW.support.Utility.ricercaDTO;

import ProjectPSW.entities.StatoFilm;
import ProjectPSW.entities.StatoVisione;
import ProjectPSW.support.Exceptions.InvalidSearchFiltersException;
import lombok.Getter;

@Getter
public class FiltriRicercaFilmVideotecaDTO extends FiltriRicercaCatalogoVideotecaDTO {

    private Integer durata;
    private String regista;


    public FiltriRicercaFilmVideotecaDTO(String titolo, String genere, Integer annoUscita, Integer valutazione, StatoVisione statoVisione, Integer durata, String regista, StatoFilm stato, boolean soloFilmPrenotabili) throws InvalidSearchFiltersException {
        super(titolo, genere, annoUscita, valutazione, statoVisione);
        if (durata < 40)
            throw new InvalidSearchFiltersException("La durata di un film non può essere inferiore ai 40 minuti.");
        this.durata = durata;
        this.regista = (regista != null && !(regista.trim().isEmpty())) ? regista.trim() : null;
    }





}
