package ProjectPSW.entities;

import ProjectPSW.support.exceptions.IllegalTVSeriesStateException;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;


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

    @NotNull(message = "L'inserimento dell'ideatore è obbligatorio per una serie TV")
    @Column(name = "Ideatore", length = 100)
    private String ideatore;

    @Setter(AccessLevel.NONE)
    @Column(name = "Data_Fine")
    private LocalDate dataFine;

    @NotNull(message = "L'inserimento dello stato di una serie TV è obbligatorio.")
    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    @Column(name = "Stato")
    private StatoSerieTV stato;



    @Builder
    public SerieTV(String imdbId, String titolo, String genere, String sinossi, String copertinaURL, LocalDate dataUscita, Integer stagioni, Integer numeroEpisodi, @NonNull String ideatore, LocalDate dataFine) {
        super(imdbId, titolo, genere, sinossi, copertinaURL, dataUscita);
        LocalDate dataOdierna = LocalDate.now();
        this.ideatore = ideatore;
        if (dataOdierna.isBefore(dataUscita)) {
            if (stagioni != null || numeroEpisodi != null || dataFine != null)
                throw new IllegalArgumentException("Se una serie non è uscita stagioni,episodi e anno di fine trasmissione non possono avere un valore definito.");
            stato = StatoSerieTV.IN_USCITA;
        }
        else {
            if (stagioni == null || numeroEpisodi == null)
                throw new IllegalArgumentException("Se una serie è uscita deve avere un numero edfinito di stagioni ed episodi.");
            if (stagioni <= 0 || numeroEpisodi <= 0)
                throw new IllegalArgumentException("Il numero di stagioni ed episodi di una serie uscita deve necessariamente essere un numero maggiore di zero.");
            if (stagioni >= numeroEpisodi && stagioni > 1)
                throw new IllegalArgumentException("Una serie della quale è uscito più di un episodio non può avere un numero di stagioni pari o superiore al suo numero di episodi.");
            this.stagioni = stagioni;
            this.numeroEpisodi = numeroEpisodi;
            if (dataFine == null)
                stato = StatoSerieTV.IN_CORSO;
            else {
                if (dataFine.isAfter(dataUscita)) {
                    this.dataFine = dataFine;
                    stato = StatoSerieTV.TERMINATO;
                }
                else
                    throw new IllegalArgumentException("Se la serie è conclusa allora il valore dell'anno l'anno di fine trasmissione non può essere un valore indefinito o inconsistente rispetto all'anno di inizio.");
            }
        }
    }


    public void aggiornaNumeroStagioni(@NonNull Integer nuovoNumeroStagioni) {
        if (stato == StatoSerieTV.IN_USCITA || nuovoNumeroStagioni <= 0 || nuovoNumeroStagioni > numeroEpisodi)
            throw new IllegalArgumentException("Il numero di stagioni non può essere inconsistente con il resto degli attributi.");
        if (!(nuovoNumeroStagioni.equals(stagioni)))
            stagioni = nuovoNumeroStagioni;
    }

    public void aggiornaNumeroEpisodi(@NonNull Integer nuovoNumeroEpisodi) {
        if (stato == StatoSerieTV.IN_USCITA || nuovoNumeroEpisodi <= 0 || nuovoNumeroEpisodi < stagioni)
            throw new IllegalArgumentException("Il numero di episodi non può essere inconsistente con il resto degli attributi.");
        if (!(nuovoNumeroEpisodi.equals(numeroEpisodi)))
            numeroEpisodi = nuovoNumeroEpisodi;
    }

    public void modificaDataFine(LocalDate nuovaDataFine) {
        if (stato != StatoSerieTV.TERMINATO)
            throw new IllegalArgumentException("Il valore della data di fine trasmissione non può essere inconsistente con il resto degli attributi");
        if (!(dataFine.equals(nuovaDataFine))) {
            if (nuovaDataFine != null) {
                if (nuovaDataFine.isAfter(dataUscita))
                    dataFine = nuovaDataFine;
                else
                    throw new IllegalArgumentException("Il valore della data di fine trasmissione non può essere antecedente a quello della data di uscita della serie.");
            }
            else {
                dataFine = null;
                stato = StatoSerieTV.IN_CORSO;
            }
        }
    }


    public void modificaStatoByData(@NonNull LocalDate nuovaDataUscita, Integer stagioniAgg, Integer numeroEpisodiAgg, LocalDate nuovaDataFine) {
        if (!(nuovaDataUscita.equals(dataUscita)))
            dataUscita = nuovaDataUscita;
        LocalDate dataOdierna = LocalDate.now();
        if (dataOdierna.isBefore(nuovaDataUscita)) {
            if (stagioniAgg != null || numeroEpisodiAgg != null || nuovaDataFine != null)
                throw new IllegalArgumentException("Se una serie non è uscita stagioni,episodi e anno di fine trasmissione non possono avere un valore definito.");
            if (stato != StatoSerieTV.IN_USCITA) {
                stagioni = null;
                numeroEpisodi = null;
                dataFine = null;
                stato = StatoSerieTV.IN_USCITA;
            }
        }
        else {
            if (stagioniAgg == null || numeroEpisodiAgg == null)
                throw new IllegalArgumentException("Se una serie è uscita deve avere un numero edfinito di stagioni ed episodi.");
            if (stagioniAgg <= 0 || numeroEpisodiAgg <= 0)
                throw new IllegalArgumentException("Il numero di stagioni ed episodi di una serie uscita deve necessariamente essere un numero maggiore di zero.");
            if (stagioniAgg >= numeroEpisodiAgg && stagioniAgg > 1)
                throw new IllegalArgumentException("Una serie della quale è uscito più di un episodio non può avere un numero di stagioni pari o superiore al suo numero di episodi.");
            if (!(stagioniAgg.equals(stagioni)))
                stagioni = stagioniAgg;
            if (!(numeroEpisodiAgg.equals(numeroEpisodi)))
                numeroEpisodi = numeroEpisodiAgg;
            if (nuovaDataFine == null) {
                if (stato == StatoSerieTV.TERMINATO)
                    dataFine = null;
                if (stato != StatoSerieTV.IN_CORSO)
                    stato = StatoSerieTV.IN_CORSO;
            }
            else {
                if (nuovaDataFine.isAfter(nuovaDataUscita)) {
                    if (!(nuovaDataFine.equals(dataFine)))
                        dataFine = nuovaDataFine;
                    if (stato != StatoSerieTV.TERMINATO)
                        stato = StatoSerieTV.TERMINATO;
                }
                else
                    throw new IllegalArgumentException("Se la serie è conclusa allora il valore dell'anno l'anno di fine trasmissione non può essere un valore indefinito o inconsistente rispetto all'anno di inizio.");
            }
        }
    }


    public void rilascioEpisodio(boolean nuovaStagione, boolean ultimoEpisodio) throws IllegalTVSeriesStateException {
        if (nuovaStagione && ultimoEpisodio)
            throw new IllegalTVSeriesStateException("Impossibile che l'ultimo episodio di una serie sia il primo di una nuova stagione");
        switch (stato) {
            case TERMINATO -> {
                throw new IllegalTVSeriesStateException("Impossibile rilasciare un nuovo episodio per una serie che è già terminata.");
            }
            case IN_CORSO -> {
                numeroEpisodi++;
                if (nuovaStagione)
                    stagioni++;
            }
            case IN_USCITA -> {
                if (dataUscita.isAfter(LocalDate.now()))
                    throw new IllegalTVSeriesStateException("Inpossibile rilasciare un episodio per una serie che non è ancora uscita");
                stato = StatoSerieTV.IN_CORSO;
                stagioni = 1;
                numeroEpisodi = 1;
            }
        }
        if (ultimoEpisodio) {
            LocalDate dataConclusione = LocalDate.now();
            stato = StatoSerieTV.TERMINATO;
            dataFine = dataConclusione;
        }
    }

































}
