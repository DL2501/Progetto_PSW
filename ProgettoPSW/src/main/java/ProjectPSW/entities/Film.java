package ProjectPSW.entities;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@DiscriminatorValue("FILM")
@Table(name = "Film")
public class Film extends Watchable {

    @Column(name = "Durata")
    private Integer durata;

    @NotNull(message = "L'inserimento del regista è obbligatorio per un film")
    @Column(name = "Regista", length = 100)
    private String regista;

    @NotNull(message = "L'inserimento della data di uscita è obbligatorio per un film")
    @Column(name = "Data_Uscita")
    private LocalDate dataUscita;


}
