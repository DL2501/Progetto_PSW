package ProjectPSW.entities;

import ProjectPSW.entities.listeners.VisioneWatchableListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@EntityListeners(VisioneWatchableListener.class)
@Table(name = "Visione_Watchable", uniqueConstraints = {@UniqueConstraint(name = "Visione_Watchable_UC", columnNames = {"Utente_ID","Watchable_ID"})}, check = {@CheckConstraint(name = "Videoteca_Check", constraint = "Utente_ID IS NOT NULL AND Watcable_ID IS NOT NULL AND (Valutazione >= 1 AND Valutazione <= 5) AND (((In_Videoteca = true) AND Stato_Visione IS NOT NULL) OR ((In_Videoteca = false) AND Valutazione IS NOT NULL AND Stato_Visione IS NULL))")})
public class Visione_Watchable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "VWID", nullable = false)
    private Integer visioneWatchableId;

    @Enumerated(EnumType.STRING)
    @Setter(AccessLevel.NONE)
    @Column(name = "Stato_Visione")
    private StatoVisione statoVisione;

    @Min(value = 1, message = "La valutazione minima è 1 stella")
    @Max(value = 5, message = "La valutazione massima è 5 stelle")
    @Setter(AccessLevel.NONE)
    @Column(name = "Valutazione")
    private Integer valutazione;

    @Column(name = "In_Videoteca", nullable = false)
    private boolean inVideoteca;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Utente_ID", nullable = false, updatable = false)
    private Utente utente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Watchable_ID", nullable = false, updatable = false)
    private Watchable watchable;


    @Builder(builderMethodName = "videotecaBuilder", builderClassName = "VideotecaBuilder")
    public Visione_Watchable(@NonNull Utente utente, @NonNull Watchable watchable, @NonNull StatoVisione statoVisione, Integer valutazione) {
        if (valutazione != null && (valutazione < 1 || valutazione > 5))
            throw new IllegalArgumentException("Il punteggio della valutazione deve essere compreso tra i valori 1 e 5.");
        this.statoVisione = statoVisione;
        this.valutazione = valutazione;
        this.utente = utente;
        this.watchable = watchable;
        inVideoteca = true;
    }


    @Builder(builderMethodName = "valutazioneBuilder", builderClassName = "ValutazioneBuilder")
    public Visione_Watchable(@NonNull Utente utente, @NonNull Watchable watchable, @NonNull Integer valutazione) {
        if (valutazione < 1 || valutazione > 5)
            throw new IllegalArgumentException("Il punteggio della valutazione deve essere compreso tra i valori 1 e 5.");
        if (watchable instanceof SerieTV)
            throw new IllegalArgumentException("Impossibile assegnare una valutazione personale ad una serie TV che non sia presente all'interno della propria videoteca personale.");
        this.utente = utente;
        this.watchable = watchable;
        this.valutazione = valutazione;
        inVideoteca = false;
    }



    public void impostaStatoVisione(@NonNull StatoVisione nuovoStatoVisione) {
        statoVisione = nuovoStatoVisione;
    }


    public void impostaValutazione(@NonNull Integer nuovaValutazione) {
        if (nuovaValutazione < 1 || nuovaValutazione > 5)
            throw new IllegalArgumentException("Il punteggio della valutazione deve essere compreso tra i valori 1 e 5.");
        valutazione = nuovaValutazione;
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
