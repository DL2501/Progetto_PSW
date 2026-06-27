package ProjectPSW.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@DiscriminatorValue("SERIE-TV")
@Table(name = "SerieTV")
public class SerieTV extends Watchable {

    @Min(value = 1, message = "Se la serie è uscita, deve avere almeno una stagione")
    @Column(name = "Stagioni")
    private Integer stagioni;

    @Min(value = 1, message = "Se la serie è uscita, deve avere almeno un episodio")
    @Column(name = "Numero_Episodi")
    private Integer numeroEpisodi;

    @NotNull(message = "L'inserimento dell'ideatore è obbligatorio per una serieTV")
    @Column(name = "Ideatore", length = 100)
    private String ideatore;

    @NotNull(message = "L'anno di inizio è obbligatorio per una serieTV")
    @Column(name = "Anno_Inizio")
    private Integer annoInizio;

    @Column(name = "Anno_Fine")
    private Integer annoFine;

    @Enumerated(EnumType.STRING)
    @Column(name = "Stato")
    private StatoSerieTV stato;

    @OneToMany(mappedBy = "serieTv", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Episodio> episodi;




}
