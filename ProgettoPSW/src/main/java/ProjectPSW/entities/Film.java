package ProjectPSW.entities;

import ProjectPSW.support.Exceptions.InvalidBookingException;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("FILM")
public class Film extends ContenutoCatalogo {

    @Min(value = 40, message = "Un film non può avere una durata minore di 40 minuti.")
    @Setter(AccessLevel.NONE)
    @Column(name = "Durata")
    private Integer durata;

    @NotNull(message = "L'inserimento del regista è obbligatorio per un film")
    @Column(name = "Regista", length = 100)
    private String regista;

    @NotNull(message = "La presenza di un conteggio del numero di valutazioni che un film ha ricevuto dagli utenti è obbligatorio")
    @Setter(AccessLevel.NONE)
    @Column(name = "Numero_Voti")
    @JsonIgnore
    private Integer numeroVoti = 0;

    @Setter(AccessLevel.NONE)
    @Column(name = "Data_Apertura_Prenotazioni")
    private LocalDate dataAperturaPrenotazioni;

    @Setter(AccessLevel.NONE)
    @Column(name = "Data_Anteprima")
    private LocalDate dataRilascioAnteprima;

    @Setter(AccessLevel.NONE)
    @Column(name = "Capacità_Massima")
    private Integer capacitaMassima;

    @Setter(AccessLevel.NONE)
    @Column(name = "Posti_Anteprima_Disponibili")
    private Integer postiAnteprimaDisponibili;

    @NotNull(message = "L'inserimento dello stato di un film è obbligatorio.")
    @Setter(AccessLevel.NONE)
    @Enumerated(EnumType.STRING)
    @Column(name = "Stato")
    private StatoFilm stato;



    @Builder
    public Film(String imdbId, String titolo, String genere, String sinossi, String copertinaURL, LocalDate dataUscita, Integer durata, @NonNull String regista) {
        super(imdbId, titolo, genere, sinossi, copertinaURL, dataUscita);
        this.regista = regista;
        LocalDate dataOdierna = LocalDate.now();
        LocalDate dataRilascioAnteprima = dataUscita.minusWeeks(1);
        LocalDate dataAperturaPrenotazioni = dataRilascioAnteprima.minusWeeks(2);
        if (dataOdierna.isBefore(dataAperturaPrenotazioni)) {
            if (durata != null)
                throw new IllegalArgumentException("Un film in uscita lontana non può avere una durata definita.");
            stato = StatoFilm.IN_USCITA;
        }
        else {
            if (durata == null || durata < 40)
                throw new IllegalArgumentException("Un film uscito o prossimo all'uscita deve avere una durata definita e superiore a 40 minuti.");
            this.durata = durata;
            if (dataOdierna.isBefore(dataRilascioAnteprima)) {
                stato = StatoFilm.PRENOTAZIONI_APERTE;
                int capMax = ThreadLocalRandom.current().nextInt(100,501);
                capacitaMassima = capMax;
                postiAnteprimaDisponibili = capMax;
            }
            else if (dataOdierna.isBefore(dataUscita))
                stato = StatoFilm.PRENOTAZIONI_CHIUSE;
            else
                stato = StatoFilm.RILASCIATO;
        }
        if (stato != StatoFilm.RILASCIATO) {
            this.dataRilascioAnteprima = dataRilascioAnteprima;
            if (stato == StatoFilm.IN_USCITA || stato == StatoFilm.PRENOTAZIONI_APERTE)
                this.dataAperturaPrenotazioni = dataAperturaPrenotazioni;
        }
    }



    public void modificaDurata(@NonNull Integer nuovaDurata) {
        if (stato == StatoFilm.IN_USCITA || nuovaDurata < 40)
            throw new IllegalArgumentException("Il valore della durata non può essere inconsistente con il resto degli attributi o inferiore a 40 minuti.");
        if (!(nuovaDurata.equals(durata)))
            durata = nuovaDurata;
    }


    public void definisciCapacitaMassima(@NonNull Integer nuovaCapacita) {
        if (stato != StatoFilm.PRENOTAZIONI_APERTE)
            throw new IllegalArgumentException("Non puoi definire un numero di posti massimo per la poriezione dell'anteprima se quest'ultima non è stata ancora programmata o è terminata.");
        if (nuovaCapacita <= 0)
            throw new IllegalArgumentException("La nuova capacità non può essere pari o inferiore a zero.");
        Integer differenza = capacitaMassima - postiAnteprimaDisponibili;
        if (nuovaCapacita < differenza)
            throw new IllegalArgumentException("La nuova capacità non può essere inferiore al numero di posti già prenotati");
        capacitaMassima = nuovaCapacita;
        postiAnteprimaDisponibili = nuovaCapacita - differenza;
    }



    public void modificaDataUscita(@NonNull LocalDate nuovaData, Integer durataFilm) {
        if (!(nuovaData.equals(dataUscita))) {
            LocalDate dataOdierna = LocalDate.now();
            LocalDate nuovaDataAnteprima = nuovaData.minusWeeks(1);
            LocalDate nuovaDataAperturaPrenotazioni = nuovaDataAnteprima.minusWeeks(2);
            if (dataOdierna.isBefore(nuovaDataAperturaPrenotazioni)) {
                if (durataFilm != null)
                    throw new IllegalArgumentException("Un film in uscita lontana non può avere una durata definita.");
                if (stato != StatoFilm.IN_USCITA) {
                    stato = StatoFilm.IN_USCITA;
                    durata = null;
                }
            }
            else {
                if (stato == StatoFilm.IN_USCITA) {
                    if (durataFilm == null || durataFilm < 40)
                        throw new IllegalArgumentException("Un film uscito o prossimo all'uscita deve avere una durata definita e superiore a 40 minuti.");
                    durata = durataFilm;
                }
                if (dataOdierna.isBefore(nuovaDataAnteprima)) {
                    if (stato != StatoFilm.PRENOTAZIONI_APERTE) {
                        stato = StatoFilm.PRENOTAZIONI_APERTE;
                        int capMax = ThreadLocalRandom.current().nextInt(100,501);
                        capacitaMassima = capMax;
                        postiAnteprimaDisponibili = capMax;
                    }
                }
                else if (dataOdierna.isBefore(nuovaData)) {
                    if (stato != StatoFilm.PRENOTAZIONI_CHIUSE)
                        stato = StatoFilm.PRENOTAZIONI_CHIUSE;
                }
                else {
                    if (stato != StatoFilm.RILASCIATO)
                        stato = StatoFilm.RILASCIATO;
                }
            }
            if (stato != StatoFilm.PRENOTAZIONI_APERTE) {
                capacitaMassima = null;
                postiAnteprimaDisponibili = null;
            }
            if (stato != StatoFilm.RILASCIATO) {
                dataRilascioAnteprima = nuovaDataAnteprima;
                if (stato == StatoFilm.IN_USCITA || stato == StatoFilm.PRENOTAZIONI_APERTE)
                    dataAperturaPrenotazioni = nuovaDataAperturaPrenotazioni;
            }
            dataUscita = nuovaData;
        }
    }


    public void apriPrenotazioni(@NonNull Integer durataFilm) {
        LocalDate dataOdierna = LocalDate.now();
        if (stato != StatoFilm.IN_USCITA || dataOdierna.isBefore(dataAperturaPrenotazioni))
            throw new IllegalStateException("Non è possibile aprire prenotazioni prima della data di apertura delle prenotazioni o per un film per il quale le prenotazioni sono già state aperte.");
        if (durataFilm < 40)
            throw new IllegalArgumentException("La durata di un film non può essere inferiore a 40 minuti.");
        durata = durataFilm;
        int capMax = ThreadLocalRandom.current().nextInt(100,501);
        capacitaMassima = capMax;
        postiAnteprimaDisponibili = capMax;
        stato = StatoFilm.PRENOTAZIONI_APERTE;
    }


    public void chiudiPrenotazioni() {
        LocalDate dataOdierna = LocalDate.now();
        if (stato != StatoFilm.PRENOTAZIONI_APERTE || dataOdierna.isBefore(dataRilascioAnteprima))
            throw new IllegalStateException("Non è possibile chiudere le prenotazioni prima della data di rilascio dell'anteprima o per un film per il quale non sono mai state aperte o sono già state chiuse.");
        dataAperturaPrenotazioni = null;
        capacitaMassima = null;
        postiAnteprimaDisponibili = null;
        stato = StatoFilm.PRENOTAZIONI_CHIUSE;
    }


    public void rilasciaFilm() {
        LocalDate dataOdierna = LocalDate.now();
        if (stato != StatoFilm.PRENOTAZIONI_CHIUSE || dataOdierna.isBefore(dataUscita))
            throw new IllegalStateException("Non è possibile rilasciare un film prima della sua data di uscita.");
        dataRilascioAnteprima = null;
        stato = StatoFilm.RILASCIATO;
    }


    public void aggiornaStato() {
        switch (stato) {
            case IN_USCITA -> {
                int durataProvvisoria = ThreadLocalRandom.current().nextInt(90,151);
                apriPrenotazioni(durataProvvisoria);
            }
            case PRENOTAZIONI_APERTE -> {
                chiudiPrenotazioni();
            }
            case PRENOTAZIONI_CHIUSE -> {
                rilasciaFilm();
            }
            case RILASCIATO -> {
                throw new IllegalStateException("Impossibile fare una transizione allo stato successivo in quanto 'RILASCIATO' è lo stato definitivo.");
            }
        }
    }



    public void prenotaPostiAnteprima(int quantita) throws InvalidBookingException {
        if (stato != StatoFilm.PRENOTAZIONI_APERTE)
            throw new InvalidBookingException("Impossibile prenotare: il film è già uscito nelle sale.");
        if (capacitaMassima == null)
            throw new InvalidBookingException("Errore: non è stata ancora programmata un anteprima per questo film.");
        if (quantita <= 0 || quantita > 4)
            throw new InvalidBookingException("Impossibile prenotare la quantità di posti richiesta: il numero minimo di posti prenotabili è 1, mentre il massimo è 4.");
        if (postiAnteprimaDisponibili < quantita)
            throw new InvalidBookingException("Impossibile prenotare la quantità di posti richiesta: non ci sono abbastanza posti per l'anteprima disponibili. Posti rimanenti: " + postiAnteprimaDisponibili);
        postiAnteprimaDisponibili -= quantita;
    }






















}
