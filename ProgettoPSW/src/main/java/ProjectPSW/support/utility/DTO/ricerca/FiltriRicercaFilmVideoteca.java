package ProjectPSW.support.utility.DTO.ricerca;

import ProjectPSW.entities.StatoFilm;
import ProjectPSW.entities.StatoVisione;
import ProjectPSW.support.exceptions.InvalidSearchFiltersException;
import lombok.Getter;

@Getter
public class FiltriRicercaFilmVideoteca extends FiltriRicercaCatalogoVideoteca {

    private Integer durata;
    private String regista;


    public FiltriRicercaFilmVideoteca(String titolo, String genere, Integer annoUscita, Integer valutazione, StatoVisione statoVisione, Integer durata, String regista, StatoFilm stato, boolean soloFilmPrenotabili) throws InvalidSearchFiltersException {
        super(titolo, genere, annoUscita, valutazione, statoVisione);
        if (durata < 40)
            throw new InvalidSearchFiltersException("La durata di un film non può essere inferiore ai 40 minuti.");
        this.durata = durata;
        this.regista = (regista != null && !(regista.trim().isEmpty())) ? regista.trim() : null;
    }





}
