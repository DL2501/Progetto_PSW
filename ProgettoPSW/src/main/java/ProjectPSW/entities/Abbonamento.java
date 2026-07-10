package ProjectPSW.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "Abbonamento")
public class Abbonamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "Abbonamento_ID", nullable = false)
    @JsonIgnore
    private Integer abbonamentoId;

    @Enumerated(EnumType.STRING)
    @Column(name = "Tipologia", nullable = false, unique = true)
    private TipoAbbonamento tipologia;

    @Min(value = 1, message = "Un abbonamento deve garantire almeno uno schermo")
    @Column(name = "Schermi_Massimi", nullable = false)
    private Integer schermiMassimi;

    @Min(value = 1, message = "Il prezzo mensile di un abbonamento deve essere superiore a zero.")
    @Column(name = "Prezzo_Mensile", nullable = false)
    private Double prezzoMensile;

    @Min(value = 1, message = "Il numero minimo di prenotazioni effettuabili tramite abbonamento non può essere minore di 1.")
    @Column(name = "Numero_Massimo_Prenotazioni", nullable = false)
    private Integer numeroPrenotazioniMassimo;

    @Builder
    public Abbonamento(@NonNull TipoAbbonamento tipologia) {
        this.tipologia = tipologia;
        switch (tipologia) {
            case BASE -> {
                schermiMassimi = 1;
                prezzoMensile = 7.99;
                numeroPrenotazioniMassimo = 1;
            }
            case STANDARD -> {
                schermiMassimi = 2;
                prezzoMensile = 12.99;
                numeroPrenotazioniMassimo = 2;
            }
            case PREMIUM -> {
                schermiMassimi = 4;
                prezzoMensile = 19.99;
                numeroPrenotazioniMassimo = 4;
            }
            default -> throw new IllegalArgumentException("Piano di abbonamento sconosciuto.");
        }
    }


    @Override
    public String toString() {
        return "Abbonamento " + tipologia;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Abbonamento a))
            return false;
        if (o == this)
            return true;
        return this.getTipologia().equals(a.getTipologia());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getTipologia());
    }
















}
