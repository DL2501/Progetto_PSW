package ProjectPSW.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Watchable", indexes = {@Index(name = "idx_Tipo_Contenuto", columnList = "Tipo_Contenuto")}, check = {@CheckConstraint(name = "Film_Check", constraint = "((In_Uscita = true) AND ((Capacita_Massima IS NULL AND Posti_Anteprima_Disponibili IS NULL) OR (Capacita_Massima > 0 AND Posti_Anteprima_Disponibili >= 0 AND Posti_Anteprima_Disponibili <= Capacita_Massima))) OR ((In_Uscita = false) AND (Durata IS NOT NULL) AND (Capacita_Massima IS NULL AND Posti_Anteprima_Disponibili IS NULL))"), @CheckConstraint(name = "SerieTV_Check", constraint = "(Stato = 'IN_USCITA' AND Stagioni IS NULL AND Numero_Episodi IS NULL AND Anno_Fine IS NULL) OR (Stato = 'IN_CORSO' AND Stagioni > 0 AND Numero_Episodi > 0 AND (Numero_Episodi > Stagioni OR Stagioni = 1) AND Anno_Fine IS NULL) OR (Stato = 'TERMINATO' AND Stagioni > 0 AND Numero_Episodi > 0 AND (Numero_Episodi > Stagioni OR Stagioni = 1) AND Anno_Fine IS NOT NULL AND Anno_Fine >= Anno_Inizio)")})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Tipo_Contenuto", discriminatorType = DiscriminatorType.STRING)
public abstract class Watchable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "Watchable_ID", nullable = false)
    @JsonIgnore
    private Integer watchableId;

    @Column(name = "IMDB_ID", unique = true, nullable = false, length = 50)
    @JsonIgnore
    private String imdbId;

    @Column(name = "Titolo", nullable = false)
    private String titolo;

    @Column(name = "Genere", nullable = false, length = 50)
    private String genere;

    @Column(name = "Sinossi", length = 2000)
    private String sinossi;

    @Column(name = "Copertina", length = 500)
    private String copertinaURL;

    @Setter(AccessLevel.NONE)
    @Column(name = "Valutazione_Media")
    private Double valutazione;

    @Setter(AccessLevel.NONE)
    @Column(name = "Numero_Voti", nullable = false)
    @JsonIgnore
    private Integer numeroVoti = 0;

    @Version
    @Setter(AccessLevel.NONE)
    @Column(name = "Version", nullable = false)
    @JsonIgnore
    private Long version;



    protected Watchable(@NonNull String imdbId, @NonNull String titolo, @NonNull String genere, String sinossi, String copertinaURL) {
        this.imdbId = imdbId;
        this.titolo = titolo;
        this.genere = genere;
        this.sinossi = sinossi;
        this.copertinaURL = copertinaURL;
    }


    @Override
    public String toString() {
        return imdbId;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Watchable w))
            return false;
        if (o == this)
            return true;
        return this.getImdbId().equals(w.getImdbId());
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(this.getImdbId());
    }

















}
