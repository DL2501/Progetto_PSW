package watchablix.entities.listeners;

import watchablix.entities.Episodio;
import watchablix.entities.Visione_Watchable;
import watchablix.entities.Watchable;
import watchablix.repositories.VisioneWatchableRepository;
import watchablix.support.exceptions.TVSeriesNotFoundException;
import watchablix.support.utility.SpringContext;
import jakarta.persistence.PrePersist;
import lombok.NonNull;
import lombok.SneakyThrows;

public class VisioneWatchableListener {


    @SneakyThrows
    @PrePersist
    public void salvataggioEpisodioCheck(@NonNull Visione_Watchable vw) {
        Watchable w = vw.getWatchable();
        if (w instanceof Episodio ep && vw.isInVideoteca()) {
            VisioneWatchableRepository visioneRepository = SpringContext.getBean(VisioneWatchableRepository.class);
            Integer utenteId = vw.getUtente().getId();
            Integer serieTvId = ep.getSerieTv().getId();
            if (visioneRepository.findByUtenteIdAndWatchableIdForUpdate(utenteId,serieTvId).isEmpty())
                throw new TVSeriesNotFoundException("Impossibile salvare un episodio nella videoteca personale in assenza della sua serie TV associata");
        }
    }










}
