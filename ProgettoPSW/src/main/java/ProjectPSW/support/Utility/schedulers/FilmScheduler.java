package ProjectPSW.support.Utility.schedulers;

import ProjectPSW.services.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FilmScheduler {

    @Autowired
    private FilmService filmService;


    @Scheduled(cron = "0 0 0 * * *")
    public void aggiornamentoStatoGiornaliero() {
        System.out.println("Avvio JOB giornaliero di aggiornamento dello stato di film non rilasciati");
        filmService.aggiornaStatoFilmNonRilasciati();
        System.out.println("Aggiornamento completato.");
    }






}
