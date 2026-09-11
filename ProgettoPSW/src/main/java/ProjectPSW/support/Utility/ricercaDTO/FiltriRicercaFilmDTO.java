package ProjectPSW.support.Utility.ricercaDTO;

import ProjectPSW.entities.StatoFilm;
import ProjectPSW.support.Exceptions.InvalidSearchFiltersException;
import lombok.Getter;

@Getter
public class FiltriRicercaFilmDTO extends FiltriRicercaCatalogoDTO {

    private Integer durata;
    private String regista;
    private StatoFilm stato;
    private boolean soloFilmPrenotabili;


    public FiltriRicercaFilmDTO(String titolo, String genere, Integer annoUscita, Integer valutazione, Integer durata, String regista, StatoFilm stato, boolean soloFilmPrenotabili) throws InvalidSearchFiltersException {
        super(titolo, genere, annoUscita, valutazione);
        if (durata < 40)
            throw new InvalidSearchFiltersException("La durata di un film non può essere inferiore ai 40 minuti.");
        this.durata = durata;
        this.regista = (regista != null && !(regista.trim().isEmpty())) ? regista.trim() : null;
        this.stato = stato;
        this.soloFilmPrenotabili = soloFilmPrenotabili;
    }









}
