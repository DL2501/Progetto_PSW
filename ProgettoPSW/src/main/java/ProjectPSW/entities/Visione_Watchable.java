package ProjectPSW.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Visione_Watchable", uniqueConstraints = {@UniqueConstraint(name = "Visione_Watchable_UC", columnNames = {"Utente_ID","Watchable_ID"})})
public class Visione_Watchable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "VWID", nullable = false)
    private Integer visioneWatchableId;

    @Enumerated(EnumType.STRING)
    @Column(name = "Stato_Visione", nullable = false)
    private StatoVisione statoVisione;

    @Min(value = 1, message = "La valutazione minima è 1 stella")
    @Max(value = 5, message = "La valutazione massima è 5 stelle")
    @Column(name = "Valutazione")
    private Integer valutazione;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Utente_ID", nullable = false, updatable = false)
    private Utente utente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Watchable_ID", nullable = false, updatable = false)
    private Watchable watchable;


    @Builder
    public Visione_Watchable(@NonNull Utente utente, @NonNull Watchable watchable, @NonNull StatoVisione statoVisione, Integer valutazione) {
        if (valutazione < 1 || valutazione > 5)
            throw new IllegalArgumentException("Il punteggio della valutazione deve essere compreso tra i valori 1 e 5.");
        this.statoVisione = statoVisione;
        this.valutazione = valutazione;
        this.utente = utente;
        this.watchable = watchable;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Visione_Watchable vw))
            return false;
        if (o == this)
            return true;
        return this.getUtente().equals(vw.getUtente()) && this.getWatchable().equals(vw.getWatchable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getUtente(),this.getWatchable());
    }




}
