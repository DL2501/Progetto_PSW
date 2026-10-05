package ProjectPSW.support.utility.DTO.ricerca;

import ProjectPSW.entities.StatoSerieTV;
import ProjectPSW.entities.StatoVisione;
import ProjectPSW.support.exceptions.InvalidSearchFiltersException;
import lombok.Getter;

@Getter
public class FiltriRicercaSerieTVVideoteca extends FiltriRicercaCatalogoVideoteca {

    private Integer numeroEpisodi;
    private String ideatore;
    private Integer annoConclusione;
    private StatoSerieTV stato;


    public FiltriRicercaSerieTVVideoteca(String titolo, String genere, Integer annoUscita, Integer valutazione, StatoVisione statoVisione, Integer numeroEpisodi, String ideatore, Integer annoConclusione, StatoSerieTV stato) throws InvalidSearchFiltersException {
        super(titolo, genere, annoUscita, valutazione, statoVisione);
        if (numeroEpisodi <= 0)
            throw new InvalidSearchFiltersException("Il numero di episodi di una serie uscita è sempre maggiore di zero.");
        this.numeroEpisodi = numeroEpisodi;
        this.ideatore = (ideatore != null && !(ideatore.trim().isEmpty())) ? ideatore.trim() : null;
        this.annoConclusione = annoConclusione;
        this.stato = stato;
    }







}
