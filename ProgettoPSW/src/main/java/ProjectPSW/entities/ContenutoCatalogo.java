package ProjectPSW.entities;

import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class ContenutoCatalogo extends Watchable {

    public ContenutoCatalogo(String imdbId, String titolo, String genere, String sinossi, String copertinaURL) {
        super(imdbId, titolo, genere, sinossi, copertinaURL);
    }

}
