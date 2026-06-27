package ProjectPSW.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@DiscriminatorValue("EPISODIO")
@Table(name = "Episodio")
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
    @Column(name = "Durata")
    private Integer durata;

    @NotNull(message = "L'inserimento del regista è obbligatorio per l'episodio")
    @Column(name = "Regista", length = 100)
    private String regista;

    @NotNull(message = "L'inserimento della data di uscita è obbligatorio per l'episodio")
    @Column(name = "Data_Uscita")
    private LocalDate dataUscita;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "L'inserimento della serieTV di appartenenza è obbligatorio per l'episodio")
    @JoinColumn(name = "SerieTV")
    private SerieTV serieTv;














}
