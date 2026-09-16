package ProjectPSW.support.utility.schedulers;

import ProjectPSW.services.SerieTVService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SerieTVScheduler {

    @Autowired
    private SerieTVService serieTVService;


    @Scheduled(cron = "0 0 0 * * *")
    public void aggiornamentoValutazioneMediaGiornaliero() {
        System.out.println("Avvio JOB giornaliero di aggiornamento della valutazione media di ogni serie TV.");
        serieTVService.aggiornaValutazioneDiTutteLeSerieTV();
        System.out.println("Aggiornamento completato.");
    }







}
