package ProjectPSW.controllers;

import ProjectPSW.entities.StatoVisione;
import ProjectPSW.entities.Visione_Watchable;
import ProjectPSW.services.UtenteService;
import ProjectPSW.services.VisioneWatchableService;
import ProjectPSW.support.exceptions.*;
import ProjectPSW.support.security.JwtUtils;
import ProjectPSW.support.utility.DTO.ricerca.FiltriRicercaCatalogoVideoteca;
import ProjectPSW.support.utility.DTO.ricerca.FiltriRicercaFilmVideoteca;
import ProjectPSW.support.utility.DTO.ricerca.FiltriRicercaSerieTVVideoteca;
import ProjectPSW.support.utility.SezioneVideoteca;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/videoteca")
@PreAuthorize("hasRole('Utente') or hasRole('Amministratore')")
public class VisioneWatchableController {

    @Autowired
    private VisioneWatchableService visioneService;

    @Autowired
    private UtenteService utenteService;


    @GetMapping
    public ResponseEntity<?> sfogliaSezionePrincipaleVideoteca(@RequestParam(defaultValue = "0") Integer numeroPagina) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            Page<Visione_Watchable> paginaSezionePrincipaleVideoteca = visioneService.sfogliaVideoteca(utenteId,numeroPagina,SezioneVideoteca.PRINCIPALE);
            return new ResponseEntity<>(paginaSezionePrincipaleVideoteca, HttpStatus.OK);
        }
        catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la visualizzazione della sezione principale della videoteca personale.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/film")
    public ResponseEntity<?> sfogliaSezioneFilmVideoteca(@RequestParam(defaultValue = "0") Integer numeroPagina) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            Page<Visione_Watchable> paginaSezioneFilmVideoteca = visioneService.sfogliaVideoteca(utenteId,numeroPagina,SezioneVideoteca.FILM);
            return new ResponseEntity<>(paginaSezioneFilmVideoteca, HttpStatus.OK);
        }
        catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la visualizzazione della sezione film della videoteca personale.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/serieTV")
    public ResponseEntity<?> sfogliaSezioneSerieTVVideoteca(@RequestParam(defaultValue = "0") Integer numeroPagina) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            Page<Visione_Watchable> paginaSezioneSerieTVVideoteca = visioneService.sfogliaVideoteca(utenteId,numeroPagina,SezioneVideoteca.SERIE_TV);
            return new ResponseEntity<>(paginaSezioneSerieTVVideoteca, HttpStatus.OK);
        }
        catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la visualizzazione della sezione serie TV della videoteca personale.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/ricerca")
    public ResponseEntity<?> ricercaGeneraleVideoteca(@RequestBody FiltriRicercaCatalogoVideoteca filtri) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            Page<Visione_Watchable> risultatiRicercaSezioneGenerale = visioneService.ricercaBaseVideoteca(utenteId,filtri);
            return new ResponseEntity<>(risultatiRicercaSezioneGenerale, HttpStatus.OK);
        }
        catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la ricerca nella sezione principale della videoteca personale.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/film/ricerca")
    public ResponseEntity<?> ricercaSezioneFilmVideoteca(@RequestBody FiltriRicercaFilmVideoteca filtri) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            Page<Visione_Watchable> risultatiRicercaSezioneFilm = visioneService.ricercaSezioneFilmVideoteca(utenteId,filtri);
            return new ResponseEntity<>(risultatiRicercaSezioneFilm, HttpStatus.OK);
        }
        catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la ricerca nella sezione film della videoteca personale.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/serieTV/ricerca")
    public ResponseEntity<?> ricercaSezioneSerieTVVideoteca(@RequestBody FiltriRicercaSerieTVVideoteca filtri) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            Page<Visione_Watchable> risultatiRicercaSezioneSerieTV = visioneService.ricercaSezioneSerieTVVideoteca(utenteId,filtri);
            return new ResponseEntity<>(risultatiRicercaSezioneSerieTV, HttpStatus.OK);
        }
        catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la ricerca nella sezione serie TV della videoteca personale.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/film/{filmId}")
    public ResponseEntity<String> aggiungiFilmAllaVideoteca(@PathVariable Integer filmId, @RequestParam StatoVisione statoVisione, @RequestParam(required = false) Integer valutazione) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            visioneService.aggiungiFilmAllaVideoteca(utenteId,filmId,statoVisione,valutazione);
            return new ResponseEntity<>("Il film selezionato è stato aggiunto alla videoteca personale con successo.", HttpStatus.CREATED);
        }
        catch (WatchableAlreadyInLibraryException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        catch (IllegalMovieStateException | IllegalRatingException | NotRatableMovieException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (UserNotFoundException | MovieNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante l'aggiunta del film selezionato alla videoteca personale.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/serieTV/{serieTvId}")
    public ResponseEntity<String> aggiungiSerieTVAllaVideoteca(@PathVariable Integer serieTvId, @RequestParam StatoVisione statoVisione, @RequestParam(required = false) Integer valutazione, @RequestParam(required = false) Integer episodiVisti, @RequestParam(required = false) boolean episodioSuccessivoInVisione) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            visioneService.aggiungiSerieTVAllaVideoteca(utenteId,serieTvId,statoVisione,valutazione,episodiVisti,episodioSuccessivoInVisione);
            return new ResponseEntity<>("La serie TV selezionata è stata aggiunta con successo alla videoteca personale.", HttpStatus.OK);
        }
        catch (WatchableAlreadyInLibraryException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        catch (IllegalRatingException | IllegalLibraryContentStateException | IllegalTVSeriesStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (UserNotFoundException | TVSeriesNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante l'aggiunta della serie TV selezionata alla videoteca personale.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/contenuti/{watchableId}")
    public ResponseEntity<?> getVisioneWatchable(@PathVariable Integer watchableId) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            Visione_Watchable contenutoVideoteca = visioneService.getVisioneWatchable(utenteId,watchableId);
            return new ResponseEntity<>(contenutoVideoteca, HttpStatus.OK);
        }
        catch (UserNotFoundException | WatchableNotInLibraryException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la visualizzazione del contenuto selezionato all'interno della videoteca personale.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/serieTV/{serieTvId}/episodi")
    public ResponseEntity<?> getAllEpisodiSerieTVInVideoteca(@PathVariable Integer serieTvId) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            List<Visione_Watchable> episodiSerieTVInVideoteca = visioneService.getAllEpisodiSerieTVInVideoteca(utenteId,serieTvId);
            return new ResponseEntity<>(episodiSerieTVInVideoteca, HttpStatus.OK);
        }
        catch (UnreleasedTVSeriesException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (UserNotFoundException | TVSeriesNotFoundException | WatchableNotInLibraryException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la visualizzazione degli episodi della serie TV selezionata nella videoteca peresonale", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/serieTV/{serieTvId}/stagioni/{numeroStagione}")
    public ResponseEntity<?> getEpisodiStagioneSerieTVInVideoteca(@PathVariable Integer serieTvId, @PathVariable Integer numeroStagione) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            List<Visione_Watchable> episodiStagioneSerieTVInVidoeteca = visioneService.getEpisodiStagioneSerieTVInVideoteca(utenteId,serieTvId,numeroStagione);
            return new ResponseEntity<>(episodiStagioneSerieTVInVidoeteca, HttpStatus.OK);
        }
        catch (IllegalTVSeriesStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (UserNotFoundException | TVSeriesNotFoundException | SeasonNotFoundException | WatchableNotInLibraryException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la visualizzazione degli episodi della stagione " + numeroStagione + " della serie TV selezionata nella videoteca personale.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/serieTV/{serieTvId}/stagioni/{numeroStagione}/episodi/{numeroEpisodio}")
    public ResponseEntity<?> getEpisodioInVideoteca(@PathVariable Integer serieTvId, @PathVariable Integer numeroStagione, @PathVariable Integer numeroEpisodio) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            Visione_Watchable episodioInVideoteca = visioneService.getEpisodioByStagioneAndNumeroInVideoteca(utenteId,serieTvId,numeroStagione,numeroEpisodio);
            return new ResponseEntity<>(episodioInVideoteca, HttpStatus.OK);
        }
        catch (IllegalTVSeriesStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (UserNotFoundException | TVSeriesNotFoundException | EpisodeNotFoundException | WatchableNotInLibraryException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la visualizzazione dell'episodio " + numeroEpisodio + " della stagione " + numeroStagione + " della serie TV selezionata.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/valutazioni-esterne")
    public ResponseEntity<?> getValutazioniEsterne() {
        try {
            Integer utenteId = getUtenteLoggatoId();
            List<Visione_Watchable> valutazioniEsterne = visioneService.mostraValutazioniEsterne(utenteId);
            return new ResponseEntity<>(valutazioniEsterne, HttpStatus.OK);
        }
        catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la visualizzazione delle valutazioni esterne alla videoteca personale.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/contenuti/{watchableId}/stato")
    public ResponseEntity<String> impostaStatoVisione(@PathVariable Integer watchableId, @RequestParam StatoVisione statoVisione) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            visioneService.impostaStatoVisioneWatchable(utenteId,watchableId,statoVisione);
            return new ResponseEntity<>("Lo stato visione del contenuto selezionato è stato aggiornato con successo.", HttpStatus.OK);
        }
        catch (UserNotFoundException | WatchableNotInLibraryException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante l'aggiornamento dello stato visione del contenuto selezionato.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/contenuti/{watchableId}/valutazione")
    public ResponseEntity<String> modificaValutazione(@PathVariable Integer watchableId, @RequestParam(required = false) Integer valutazione) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            visioneService.modificaValutazioneWatchable(utenteId,watchableId,valutazione);
            return new ResponseEntity<>("La valutazione del contenuto selezionato è stata aggiornata con successo.", HttpStatus.OK);
        }
        catch (IllegalRatingException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (UserNotFoundException | WatchableNotFoundException | WatchableNotInLibraryException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la modifica della valutazione del contenuto selezionato.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/film/{filmId}")
    public ResponseEntity<String> rimuoviFilm(@PathVariable Integer filmId) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            visioneService.rimuoviFilmDallaVideoteca(utenteId,filmId);
            return new ResponseEntity<>("Il film selezionato è stato rimosso con successo dalla videoteca personale.", HttpStatus.OK);
        }
        catch (UserNotFoundException | MovieNotFoundException | WatchableNotInLibraryException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la rimozione del film selezionato all'interno della videoteca personale.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/serieTV/{serieTvId}")
    public ResponseEntity<String> rimuoviSerieTV(@PathVariable Integer serieTvId) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            visioneService.rimuoviSerieTVDallaVideoteca(utenteId,serieTvId);
            return new ResponseEntity<>("La serie TV selezionata è stata rimossa con successo dalla videoteca personale.", HttpStatus.OK);
        }
        catch (UserNotFoundException | TVSeriesNotFoundException | WatchableNotInLibraryException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la rimozione della serie TV selezionata dalla videoteca personale.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping
    public ResponseEntity<String> svuotaVideoteca() {
        try {
            Integer utenteId = getUtenteLoggatoId();
            visioneService.svuotaVideoteca(utenteId);
            return new ResponseEntity<>("La tua videoteca personale è stata svuotata con successo.", HttpStatus.OK);
        }
        catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante lo svuotamento della tua videoteca perosnale.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping
    public ResponseEntity<String> eliminaVideoteca() {
        try {
            Integer utenteId = getUtenteLoggatoId();
            visioneService.eliminaVideoteca(utenteId);
            return new ResponseEntity<>("La videoteca personale dell'utente selezionato è stata eliminata con successo.", HttpStatus.OK);
        }
        catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante l'eliminazione della videoteca personale dell'utente selezionato.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/serieTV/{serieTvId}/valutazione")
    public ResponseEntity<String> aggiornaValutazioneSerieTVByMedia(@PathVariable Integer serieTvId) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            visioneService.aggiornaValutazioneSerieTVByMedia(utenteId,serieTvId);
            return new ResponseEntity<>("La valutazione della serie TV selezionata è stata aggiornata con successo attraverso la media delle tue valutazioni ai singoli episodi.", HttpStatus.OK);
        }
        catch (UserNotFoundException | TVSeriesNotFoundException | WatchableNotInLibraryException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante l'aggiornamento della valutazione della serie TV selezionata.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    private Integer getUtenteLoggatoId() throws UserNotFoundException {
        String nomeUtente = JwtUtils.getNomeUtente();
        return utenteService.getUtente(nomeUtente).getUtenteId();
    }




}
