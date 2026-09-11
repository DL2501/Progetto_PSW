package ProjectPSW.entities.listeners;

import ProjectPSW.entities.Episodio;
import ProjectPSW.entities.Visione_Watchable;
import ProjectPSW.entities.Watchable;
import ProjectPSW.repositories.VisioneWatchableRepository;
import ProjectPSW.support.Exceptions.TVSeriesNotFoundException;
import ProjectPSW.support.Utility.SpringContext;
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
            Integer utenteId = vw.getUtente().getUtenteId();
            Integer serieTvId = ep.getSerieTv().getWatchableId();
            if (visioneRepository.findByUtenteIdAndWatchableIdForUpdate(utenteId,serieTvId).isEmpty())
                throw new TVSeriesNotFoundException("Impossibile salvare un episodio nella videoteca personale in assenza della sua serie TV associata");
        }
    }










}
