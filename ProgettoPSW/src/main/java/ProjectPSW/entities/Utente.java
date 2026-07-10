package ProjectPSW.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Utente")
public class Utente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "UID", nullable = false)
    @JsonIgnore
    private Integer utenteId;

    @Column(name = "Nome_Utente", unique = true, nullable = false, updatable = false)
    private String nomeUtente;

    @Column(name = "Nome", length = 50)
    private String nome;

    @Column(name = "Cognome", length = 50)
    private String cognome;

    @Column(name = "Email", nullable = false, unique = true, length = 90)
    private String email;

    @Column(name = "Data_Nascita", nullable = false)
    private LocalDate dataNascita;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "Abbonamento")
    private Abbonamento abbonamento;

    @OneToMany(mappedBy = "utente", cascade = CascadeType.PERSIST)
    @Setter(AccessLevel.NONE)
    @JsonIgnore
    private List<Prenotazione> prenotazioni;


    @Builder
    public Utente(@NonNull String nomeUtente, String nome, String cognome, @NonNull String email, @NonNull LocalDate dataNascita, Abbonamento abbonamento){
        this.nomeUtente = nomeUtente;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.dataNascita = dataNascita;
        this.abbonamento = abbonamento;
        prenotazioni = new ArrayList<>();
    }


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
        return this.getNomeUtente().equals(u.getNomeUtente());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getNomeUtente());
    }











}
