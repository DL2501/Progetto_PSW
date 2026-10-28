package watchablix.support.utility.dto.request;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;
import watchablix.entities.Episodio;
import watchablix.entities.SerieTV;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

@Getter
public class SerieTVRequest {

    private SerieTV serie;
    private List<Episodio> episodi;


    @Jacksonized
    @Builder
    public SerieTVRequest(@NonNull SerieTV serie, List<Episodio> episodi) {
        this.serie = serie;
        this.episodi = episodi;
    }



}
