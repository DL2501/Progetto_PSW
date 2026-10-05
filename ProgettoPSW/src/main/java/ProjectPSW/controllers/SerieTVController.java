package ProjectPSW.controllers;

import ProjectPSW.entities.Episodio;
import ProjectPSW.entities.SerieTV;
import ProjectPSW.services.SerieTVService;
import ProjectPSW.services.UtenteService;
import ProjectPSW.support.exceptions.*;
import ProjectPSW.support.security.JwtUtils;
import ProjectPSW.support.utility.DTO.request.SerieTVRequest;
import ProjectPSW.support.utility.DTO.ricerca.FiltriRicercaSerieTV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/serieTV")
public class SerieTVController {

    @Autowired
    private SerieTVService serieTVService;

    @Autowired
    private UtenteService utenteService;


    @PostMapping("/ricerca")
    public ResponseEntity<?> ricercaSezioneSerieTV(@RequestBody FiltriRicercaSerieTV filtri) {
        try {
            Page<SerieTV> risultatiRicerca = serieTVService.ricercaSezioneSerieTV(filtri);
            return new ResponseEntity<>(risultatiRicerca, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la ricerca.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/episodi")
    @PreAuthorize("hasRole('Amministratore')")
    public ResponseEntity<String> aggiungiEpisodio(@RequestBody Episodio episodio, @RequestParam boolean nuovaStagione, @RequestParam boolean ultimoEpisodio) {
        try {
            serieTVService.aggiungiEpisodio(episodio,nuovaStagione,ultimoEpisodio);
            return new ResponseEntity<>("L'episodio è stato aggiunto con successo.", HttpStatus.CREATED);
        }
        catch (EpisodeAlreadyExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        catch (IllegalTVSeriesStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (TVSeriesNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante il salvataggio dell'episodio.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping
    @PreAuthorize("hasRole('Amministratore')")
    public ResponseEntity<String> aggiungiSerieTV(@RequestBody SerieTVRequest request) {
        try {
            SerieTV serie = request.getSerie();
            List<Episodio> episodi = request.getEpisodi();
            if (episodi == null || episodi.isEmpty())
                serieTVService.aggiungiSerieTVInUscita(serie);
            else
                serieTVService.aggiungiSerieTV(serie,episodi);
            return new ResponseEntity<>("La serie TV è stata aggiunta con successo.", HttpStatus.CREATED);
        }
        catch (TVSeriesAlreadyExistException | EpisodeAlreadyExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        catch (IllegalTVSeriesStateException | InvalidEpisodeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante il salvataggio della serie.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{serieTvId}")
    public ResponseEntity<?> getDettaglioSerieTV(@PathVariable Integer serieTvId) {
        try {
            SerieTV serie = serieTVService.getSerieTV(serieTvId);
            return new ResponseEntity<>(serie, HttpStatus.OK);
        } catch (TVSeriesNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la visualizzazione dei dettagli della serieTV.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping
    public ResponseEntity<?> sfogliaSezioneSerieTV(@RequestParam(defaultValue = "0") Integer numeroPagina) {
        try {
            Page<SerieTV> paginaSezioneSerieTV = serieTVService.sfogliaSezioneSerieTV(numeroPagina);
            return new ResponseEntity<>(paginaSezioneSerieTV, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore nella visualizzazione della sezione serie TV del catalogo principale.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{serieTvId}/episodi")
    public ResponseEntity<?> getAllEpisodi(@PathVariable Integer serieTvId) {
        try {
            List<Episodio> episodiSerie = serieTVService.getAllEpisodi(serieTvId);
            return new ResponseEntity<>(episodiSerie, HttpStatus.OK);
        }
        catch (TVSeriesNotFoundException | UnreleasedTVSeriesException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return new ResponseEntity<>("Errore durante la viusalizzazione degli episodi della serie TV selezionata.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{serieTvId}/stagioni/{numeroStagione}")
    public ResponseEntity<?> getEpisodiStagione(@PathVariable Integer serieTvId, @PathVariable Integer numeroStagione) {
        try {
            List<Episodio> episodiStagione = serieTVService.getEpisodiStagione(serieTvId,numeroStagione);
            return new ResponseEntity<>(episodiStagione, HttpStatus.OK);
        }
        catch (TVSeriesNotFoundException | SeasonNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalTVSeriesStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la visualizzazione degli episodi della stagione " + numeroStagione + " della serie TV selezionata.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{serieTvId}/stagioni/{numeroStagione}/episodi/{numeroEpisodio}")
    public ResponseEntity<?> getEpisodio(@PathVariable Integer serieTvId, @PathVariable Integer numeroStagione, @PathVariable Integer numeroEpisodio) {
        try {
            Episodio episodio = serieTVService.getEpisodioByStagioneAndNumero(serieTvId,numeroStagione,numeroEpisodio);
            return new ResponseEntity<>(episodio, HttpStatus.OK);
        }
        catch (TVSeriesNotFoundException | EpisodeNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalTVSeriesStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la visualizzazione dell'episodio " + numeroEpisodio + " della stagione " + numeroStagione + " della serie TV selezionata.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/episodi/{episodioId}")
    @PreAuthorize("hasRole('Amministratore')")
    public ResponseEntity<String> eliminaEpisodio(@PathVariable Integer episodioId) {
        try {
            serieTVService.eliminaEpisodio(episodioId);
            return new ResponseEntity<>("L'episodio selezionato è stato eliminato con successo.", HttpStatus.OK);
        } catch (EpisodeNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante l'eliminazione dell'episodio selezionato.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{serieTvId}")
    @PreAuthorize("hasRole('Amministratore')")
    public ResponseEntity<String> eliminaSerieTV(@PathVariable Integer serieTvId) {
        try {
            serieTVService.eliminaSerieTV(serieTvId);
            return new ResponseEntity<>("La serie TV selezionata è stata eliminata con successo.", HttpStatus.OK);
        }
        catch (TVSeriesNotFoundException | EpisodeNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (Exception e) {
            return new ResponseEntity<>("Errore durante l'eliminazione della serie TV selezionata.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/episodi/{episodioId}/valuta")
    @PreAuthorize("hasRole('Utente') or hasRole('Amministratore')")
    public ResponseEntity<String> valutaEpisodio(@PathVariable Integer episodioId, @RequestParam Integer valutazione) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            serieTVService.valutaEpisodio(utenteId,episodioId,valutazione);
            return new ResponseEntity<>("La tua valutazione è stata aggiunta con successo.", HttpStatus.OK);
        }
        catch (IllegalRatingException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (WatchableAlreadyRatedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        catch (EpisodeNotFoundException | UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la valutazione dell'episodio selezionato.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/{serieTvId}/valutazione")
    @PreAuthorize("hasRole('Amministratore')")
    public ResponseEntity<String> aggiornaValutazione(@PathVariable Integer serieTvId) {
        try {
            serieTVService.aggiornaValutazioneSerieTV(serieTvId);
            return new ResponseEntity<>("La valuazione della serie TV selezionata è stata aggiornata con successo.", HttpStatus.OK);
        } catch (TVSeriesNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante l'aggiornamento della valutazione della serie TV selezionata.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/valutazione")
    @PreAuthorize("hasRole('Amministratore')")
    public ResponseEntity<String> aggiornaTutteLeValutazioni() {
        try {
            serieTVService.aggiornaValutazioneDiTutteLeSerieTV();
            return new ResponseEntity<>("Le valutazioni di tutte le serie TV presenti nel catalogo sono state aggiornate con successo.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante l'aggiornamento delle valutazioni delle serie TV presenti nel catalogo.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    private Integer getUtenteLoggatoId() throws UserNotFoundException {
        String nomeUtente = JwtUtils.getNomeUtente();
        return utenteService.getUtente(nomeUtente).getUtenteId();
    }






}
