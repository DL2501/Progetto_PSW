package ProjectPSW.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class VisionePK implements Serializable {

    @Column(name = "Utente_ID", nullable = false, updatable = false)
    private Integer utenteId;

    @Column(name = "Watchable_ID", nullable = false, updatable = false)
    private Integer watchableId;



}
