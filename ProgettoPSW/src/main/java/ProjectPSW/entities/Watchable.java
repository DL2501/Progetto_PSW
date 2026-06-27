package ProjectPSW.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;


@Getter
@Setter
@Entity
@Table(name = "Watchable", indexes = {@Index(name = "idx_Tipo_Contenuto", columnList = "Tipo_Contenuto")})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "Tipo_Contenuto", discriminatorType = DiscriminatorType.STRING)
public abstract class Watchable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "watchable", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Visione_Watchable> utentiAssociati;


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Watchable w))
            return false;
        if (o == this)
            return true;
        return imdbId.equals(w.getImdbId());
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(imdbId);
    }

















}
