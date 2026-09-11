package ProjectPSW.support.Utility.ricercaDTO;

import ProjectPSW.entities.StatoSerieTV;
import ProjectPSW.entities.StatoVisione;
import ProjectPSW.support.Exceptions.InvalidSearchFiltersException;
import lombok.Getter;

@Getter
public class FiltriRicercaSerieTVVideotecaDTO extends FiltriRicercaCatalogoVideotecaDTO {

    private Integer numeroEpisodi;
    private String ideatore;
    private Integer annoConclusione;
    private StatoSerieTV stato;


    public FiltriRicercaSerieTVVideotecaDTO(String titolo, String genere, Integer annoUscita, Integer valutazione, StatoVisione statoVisione, Integer numeroEpisodi, String ideatore, Integer annoConclusione, StatoSerieTV stato) throws InvalidSearchFiltersException {
        super(titolo, genere, annoUscita, valutazione, statoVisione);
        if (numeroEpisodi <= 0)
            throw new InvalidSearchFiltersException("Il numero di episodi di una serie uscita è sempre maggiore di zero.");
        this.numeroEpisodi = numeroEpisodi;
        this.ideatore = (ideatore != null && !(ideatore.trim().isEmpty())) ? ideatore.trim() : null;
        this.annoConclusione = annoConclusione;
        this.stato = stato;
    }







}
