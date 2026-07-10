package ProjectPSW.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("FILM")
public class Film extends ContenutoCatalogo {

    @Min(value = 40, message = "Un film non può avere una durata minore di 40 minuti.")
    @Column(name = "Durata")
    private Integer durata;

    @NotNull(message = "L'inserimento del regista è obbligatorio per un film")
    @Column(name = "Regista", length = 100)
    private String regista;

    @NotNull(message = "L'inserimento della data di uscita è obbligatorio per un film")
    @Column(name = "Data_Uscita")
    private LocalDate dataUscita;

    @Setter(AccessLevel.NONE)
    @Column(name = "Capacità_Massima")
    private Integer capacitaMassima;

    @Setter(AccessLevel.NONE)
    @Column(name = "Posti_Anteprima_Disponibili")
    private Integer postiAnteprimaDisponibili;

    @NotNull(message = "Dichiarare se il film è uscito o è in uscita rappresenta un dato obbligatorio da inserire.")
    @Column(name = "In_Uscita")
    private Boolean inUscita;



    @Builder
    public Film(String imdbId, String titolo, String genere, String sinossi, String copertinaURL, Integer durata, @NonNull String regista, @NonNull LocalDate dataUscita, Integer capacitaMassima, @NonNull Boolean inUscita) {
        super(imdbId, titolo, genere, sinossi, copertinaURL);
        this.inUscita = inUscita;
        this.regista = regista;
        this.dataUscita = dataUscita;
        if (!this.inUscita) {
            if (durata == null || durata < 40)
                throw new IllegalArgumentException("Un film uscito deve avere una durata definita e superiore a 40 minuti.");
            if (capacitaMassima != null)
                throw new IllegalArgumentException("Un film uscito non può avere posti in anteprima.");
            this.durata = durata;
        }
        else {
            if (durata != null && durata < 40)
                throw new IllegalArgumentException("Se nota, la durata di un film in uscita non può essere inferiore a 40 minuti.");
            this.durata = durata;
            if (capacitaMassima != null) {
                if (capacitaMassima > 0) {
                    this.capacitaMassima = capacitaMassima;
                    this.postiAnteprimaDisponibili = capacitaMassima;
                }
                else
                    throw new IllegalArgumentException("Se nota, la capacità massima deve essere maggiore di zero.");
            }
        }
    }



    public void definisciCapacitaMassima(@NonNull Integer nuovaCapacita) {
        if (!inUscita)
            throw new IllegalArgumentException("Non puoi definire un numero di posti massimo per l'anteprima se il film in questione è già uscito");
        if (nuovaCapacita <= 0)
            throw new IllegalArgumentException("La nuova capacità non può essere pari o inferiore a zero.");
        if (postiAnteprimaDisponibili != null) {
            Integer differenza = capacitaMassima - postiAnteprimaDisponibili;
            if (nuovaCapacita < differenza)
                throw new IllegalArgumentException("La nuova capacità non può essere inferiore al numero di posti già prenotati");
            capacitaMassima = nuovaCapacita;
            postiAnteprimaDisponibili = nuovaCapacita - differenza;
        }
        else {
            capacitaMassima = nuovaCapacita;
            postiAnteprimaDisponibili = nuovaCapacita;
        }
    }


    public void chiudiPrevendita() {
        if (capacitaMassima != null) {
            if (inUscita)
                throw new IllegalStateException("Non puoi annullare prevendite e prenotazioni di un film che non è ancora uscito.");
            capacitaMassima = null;
            postiAnteprimaDisponibili = null;
        }
    }




























}
