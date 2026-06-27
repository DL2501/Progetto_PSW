package ProjectPSW.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "Visione_Watchable")
public class Visione_Watchable {

    @EmbeddedId
    private VisionePK vpk;

    @Enumerated(EnumType.STRING)
    @Column(name = "Stato_Visione", nullable = false)
    private StatoVisione statoVisione;

    @Min(value = 1, message = "La valutazione minima è 1 stella")
    @Max(value = 5, message = "La valutazione massima è 5 stelle")
    @Column(name = "Valutazione")
    private Integer valutazione;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("utenteId")
    @JoinColumn(name = "Utente_ID")
    private Utente utente;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("watchableId")
    @JoinColumn(name = "Watchable_ID")
    private Watchable watchable;


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Visione_Watchable vw))
            return false;
        if (o == this)
            return true;
        return vpk.equals(vw.getVpk());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(vpk);
    }




}
