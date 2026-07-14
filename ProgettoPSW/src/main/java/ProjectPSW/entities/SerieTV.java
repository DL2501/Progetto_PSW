package ProjectPSW.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("SERIE-TV")
public class SerieTV extends ContenutoCatalogo {

    @Min(value = 1, message = "Se la serie è uscita, deve avere almeno una stagione")
    @Setter(AccessLevel.NONE)
    @Column(name = "Stagioni")
    private Integer stagioni;

    @Min(value = 1, message = "Se la serie è uscita, deve avere almeno un episodio")
    @Setter(AccessLevel.NONE)
    @Column(name = "Numero_Episodi")
    private Integer numeroEpisodi;

    @NotNull(message = "L'inserimento dell'ideatore è obbligatorio per una serieTV")
    @Column(name = "Ideatore", length = 100)
    private String ideatore;

    @Setter(AccessLevel.NONE)
    @Column(name = "Data_Fine")
    private LocalDate dataFine;

    @NotNull(message = "L'inserimento dello stato di una serieTV è obbligatorio.")
    @Enumerated(EnumType.STRING)
    @Column(name = "Stato")
    private StatoSerieTV stato;

    @OneToMany(mappedBy = "serieTv", cascade = CascadeType.ALL, orphanRemoval = true)
    @Setter(AccessLevel.NONE)
    List<Episodio> episodi;



    @Builder
    public SerieTV(String imdbId, String titolo, String genere, String sinossi, String copertinaURL, LocalDate dataUscita, Integer stagioni, Integer numeroEpisodi, @NonNull String ideatore, LocalDate dataFine, @NonNull StatoSerieTV stato) {
        super(imdbId, titolo, genere, sinossi, copertinaURL, dataUscita);
        this.ideatore = ideatore;
        this.stato = stato;
        if (!(this.stato.equals(StatoSerieTV.IN_USCITA))) {
            if (stagioni == null || numeroEpisodi == null)
                throw new IllegalArgumentException("Se una serie è uscita deve avere un numero edfinito di stagioni ed episodi.");
            if (stagioni <= 0 || numeroEpisodi <= 0)
                throw new IllegalArgumentException("Il numero di stagioni ed episodi di una serie uscita deve necessariamente essere un numero maggiore di zero.");
            if (stagioni >= numeroEpisodi && stagioni > 1)
                throw new IllegalArgumentException("Una serie della quale è uscito più di un episodio non può avere un numero di stagioni pari o superiore al suo numero di episodi.");
            this.stagioni = stagioni;
            this.numeroEpisodi = numeroEpisodi;
            if (this.stato.equals(StatoSerieTV.TERMINATO)) {
                if (dataFine != null && dataFine.isAfter(dataUscita))
                    this.dataFine = dataFine;
                else
                    throw new IllegalArgumentException("Se la serie è conclusa allora il valore dell'anno l'anno di fine trasmissione non può essere un valore indefinito o inconsistente rispetto all'anno di inizio.");
            }
        }
        else {
            if (stagioni != null || numeroEpisodi != null || dataFine != null)
                throw new IllegalArgumentException("Se una serie non è uscita stagioni,episodi e anno di fine trasmissione non possono avere un valore definito.");
        }
        episodi = new ArrayList<>();
    }


    public void aggiornaNumeroStagioni(@NonNull Integer nuovoNumeroStagioni) {
        if (stato.equals(StatoSerieTV.IN_USCITA) || nuovoNumeroStagioni <= 0 || nuovoNumeroStagioni > numeroEpisodi)
            throw new IllegalArgumentException("Il numero di stagioni non può essere inconsistente con il resto degli attributi.");
        stagioni = nuovoNumeroStagioni;
    }

    public void aggiornaNumeroEpisodi(@NonNull Integer nuovoNumeroEpisodi) {
        if (stato.equals(StatoSerieTV.IN_USCITA) || nuovoNumeroEpisodi <= 0 || nuovoNumeroEpisodi < stagioni)
            throw new IllegalArgumentException("Il numero di episodi non può essere inconsistente con il resto degli attributi.");
        numeroEpisodi = nuovoNumeroEpisodi;
    }

    public void inserisciDataFine(@NonNull LocalDate dataConclusione) {
        if (!(stato.equals(StatoSerieTV.TERMINATO)) || dataConclusione.isBefore(dataUscita))
            throw new IllegalArgumentException("Il valore dell'anno di fine trasmissione non può essere inconsistenete con il resto degli attributi.");
        dataFine = dataConclusione;
    }










}
