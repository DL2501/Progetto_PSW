package ProjectPSW.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@DiscriminatorValue("EPISODIO")
public class Episodio extends Watchable {

    @NotNull(message = "L'inserimento della satgione di appartenenza è obbligatorio per l'episodio")
    @Min(value = 1, message = "Il numero della stagione non può essere minore di 1")
    @Column(name = "Stagione")
    private Integer stagione;

    @NotNull(message = "L'inserimento del numero è obbligatorio per l'episodio")
    @Min(value = 1, message = "Il numero dell'episodio all'interno di una stagione non può essere minore di 1")
    @Column(name = "Numero")
    private Integer numero;

    @NotNull(message = "L'inserimento della durata in minuti è obbligatorio per l'episodio")
    @Min(value = 1, message = "La durata di un episodio deve essere di almeno un minuto.")
    @Column(name = "Durata")
    private Integer durata;

    @NotNull(message = "L'inserimento del regista è obbligatorio per l'episodio")
    @Column(name = "Regista", length = 100)
    private String regista;

    @NotNull(message = "L'inserimento della data di uscita è obbligatorio per l'episodio")
    @Column(name = "Data_Uscita")
    private LocalDate dataUscita;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @NotNull(message = "L'inserimento della serieTV di appartenenza è obbligatorio per l'episodio")
    @JoinColumn(name = "SerieTV", nullable = false, updatable = false)
    private SerieTV serieTv;


    @Builder
    public Episodio(String imdbId, String titolo, String genere, String sinossi, String copertinaURL, @NonNull Integer stagione, @NonNull Integer numero, @NonNull Integer durata, @NonNull String regista, @NonNull LocalDate dataUscita, @NonNull SerieTV serieTv) {
        super(imdbId, titolo, genere, sinossi, copertinaURL);
        if (stagione <= 0 || numero <= 0 || durata <= 0)
            throw new IllegalArgumentException("I valori di stagione,numero episodio e durata devono avere valori maggiori di zero.");
        this.stagione = stagione;
        this.numero = numero;
        this.durata = durata;
        this.regista = regista;
        this.dataUscita = dataUscita;
        this.serieTv = serieTv;
    }


}
