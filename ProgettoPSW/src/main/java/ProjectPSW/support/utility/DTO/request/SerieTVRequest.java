package ProjectPSW.support.utility.DTO.request;

import ProjectPSW.entities.Episodio;
import ProjectPSW.entities.SerieTV;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
public class SerieTVRequest {

    private SerieTV serie;
    private List<Episodio> episodi;


    public SerieTVRequest(@NonNull SerieTV serie, List<Episodio> episodi) {
        this.serie = serie;
        this.episodi = episodi;
    }



}
