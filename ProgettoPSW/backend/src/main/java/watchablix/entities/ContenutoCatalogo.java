package watchablix.entities;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ContenutoCatalogo extends Watchable {

    protected ContenutoCatalogo(String imdbId, String titolo, String genere, String sinossi, String copertinaURL, LocalDate dataUscita) {
        super(imdbId, titolo, genere, sinossi, copertinaURL, dataUscita);
    }




}
