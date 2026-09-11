package ProjectPSW.support.Utility.ricercaDTO;

import ProjectPSW.support.Exceptions.InvalidSearchFiltersException;
import lombok.Getter;

@Getter
public class FiltriRicercaCatalogoDTO {

    private String titolo;
    private String genere;
    private Integer annoUscita;
    private Integer valutazione;
    private int numeroPagina = 0;
    private int elementiPerPagina = 20;


    public FiltriRicercaCatalogoDTO(String titolo, String genere, Integer annoUscita, Integer valutazione) throws InvalidSearchFiltersException {
        if (valutazione != null && (valutazione < 1 || valutazione > 5))
            throw new InvalidSearchFiltersException("La valutazione di un contenuto deve essere compresa tra 1 e 5.");
        this.titolo = (titolo != null && !(titolo.trim().isEmpty())) ? titolo.trim() : null;
        this.genere = (genere != null && !(genere.trim().isEmpty())) ? genere.trim() : null;
        this.annoUscita = annoUscita;
        this.valutazione = valutazione;
    }




}
