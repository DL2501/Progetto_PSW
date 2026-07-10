package ProjectPSW.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Prenotazione")
public class Prenotazione {

    public static final Double PREZZO_FISSO_BIGLIETTO = 8.50;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "Prenotazione_ID", nullable = false)
    @JsonIgnore
    private Integer prenotazioneId;

    @Column(name = "Codice_Biglietto", nullable = false, unique = true, updatable = false, length = 40)
    private String codiceBiglietto;

    @Column(name = "Data_Prenotazione", nullable = false, updatable = false)
    private LocalDateTime dataPrenotazione;

    @Min(value = 1, message = "Il prezzo pagato per un biglietto deve essere maggiore di zero.")
    @Setter(AccessLevel.NONE)
    @Column(name = "Prezzo_Pagato", nullable = false, updatable = false)
    private Double prezzoPagato;

    @Column(name = "Validità", nullable = false)
    private boolean valida;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Utente_ID", nullable = false, updatable = false)
    private Utente utente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Film_ID", nullable = false, updatable = false)
    private Film film;


    @Builder
    public Prenotazione(@NonNull Utente utente, @NonNull Film film) {
        codiceBiglietto = "PR-" + UUID.randomUUID();
        dataPrenotazione = LocalDateTime.now();
        prezzoPagato = PREZZO_FISSO_BIGLIETTO;
        valida = true;
        this.utente = utente;
        this.film = film;
    }

    public void annullaPrenotazione() {
        if (!valida)
            throw new IllegalStateException("Non puoi annullare una prenotazione che è già stata annullata.");
        valida = false;
    }


    @Override
    public String toString() {
        return codiceBiglietto;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Prenotazione p))
            return false;
        if (o == this)
            return true;
        return this.getCodiceBiglietto().equals(p.getCodiceBiglietto());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getCodiceBiglietto());
    }


























}
