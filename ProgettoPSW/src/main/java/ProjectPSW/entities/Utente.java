package ProjectPSW.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "Utente")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UID", nullable = false)
    @JsonIgnore
    private Integer utenteId;

    @Column(name = "Nome_Utente", unique = true, nullable = false, updatable = false)
    private String nomeUtente;

    @Column(name = "Nome", length = 50)
    private String nome;

    @Column(name = "Cognome", length = 50)
    private String cognome;

    @Column(name = "Data_Nascita", nullable = false)
    private LocalDate dataNascita;

    @OneToMany(mappedBy = "utente", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Visione_Watchable> videotecaVirtuale;


    @Transient
    public int getEta() {
        if (dataNascita == null)
            return -1;
        return Period.between(dataNascita,LocalDate.now()).getYears();
    }

    @Override
    public String toString() {
        return nomeUtente;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Utente u))
            return false;
        if (o == this)
            return true;
        return nomeUtente.equals(u.getNomeUtente());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nomeUtente);
    }











}
