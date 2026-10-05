package ProjectPSW.support.utility.DTO.ricerca;

import ProjectPSW.entities.StatoFilm;
import ProjectPSW.support.exceptions.InvalidSearchFiltersException;
import lombok.Getter;

@Getter
public class FiltriRicercaFilm extends FiltriRicercaCatalogo {

    private Integer durata;
    private String regista;
    private StatoFilm stato;
    private boolean soloFilmPrenotabili;


    public FiltriRicercaFilm(String titolo, String genere, Integer annoUscita, Integer valutazione, Integer durata, String regista, StatoFilm stato, boolean soloFilmPrenotabili) throws InvalidSearchFiltersException {
        super(titolo, genere, annoUscita, valutazione);
        if (durata < 40)
            throw new InvalidSearchFiltersException("La durata di un film non può essere inferiore ai 40 minuti.");
        this.durata = durata;
        this.regista = (regista != null && !(regista.trim().isEmpty())) ? regista.trim() : null;
        this.stato = stato;
        this.soloFilmPrenotabili = soloFilmPrenotabili;
    }









}
