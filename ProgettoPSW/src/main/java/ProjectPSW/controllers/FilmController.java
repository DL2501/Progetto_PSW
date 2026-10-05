package ProjectPSW.controllers;

import ProjectPSW.entities.Film;
import ProjectPSW.services.FilmService;
import ProjectPSW.services.UtenteService;
import ProjectPSW.support.exceptions.*;
import ProjectPSW.support.security.JwtUtils;
import ProjectPSW.support.utility.DTO.ricerca.FiltriRicercaFilm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/film")
public class FilmController {

    @Autowired
    private FilmService filmService;

    @Autowired
    private UtenteService utenteService;


    @PostMapping("/ricerca")
    public ResponseEntity<?> ricercaSezioneFilm(@RequestBody FiltriRicercaFilm filtri) {
        try {
            Page<Film> risultatiRicerca = filmService.ricercaSezioneFilm(filtri);
            return new ResponseEntity<>(risultatiRicerca, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la ricerca.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping
    @PreAuthorize("hasRole('Amministratore')")
    public ResponseEntity<String> aggiungiFilm(@RequestBody Film f) {
        try {
            filmService.aggiungiFilm(f);
            return new ResponseEntity<>("Il film è stato aggiunto con successo.", HttpStatus.CREATED);
        } catch (MovieAlreadyExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante il salvataggio del film.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{filmId}")
    public ResponseEntity<?> getDettaglioFilm(@PathVariable Integer filmId) {
        try {
            Film film = filmService.getFilm(filmId);
            return new ResponseEntity<>(film, HttpStatus.OK);
        } catch (MovieNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore nella visualizzazione dei dettagli del film.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping
    public ResponseEntity<?> sfogliaSezioneFilm(@RequestParam(defaultValue = "0") Integer numeroPagina) {
        try {
            Page<Film> paginaSezioneFilm = filmService.sfogliaSezioneFilm(numeroPagina);
            return new ResponseEntity<>(paginaSezioneFilm, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore nella visualizzazione della sezione film del catalogo principale.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{filmId}")
    @PreAuthorize("hasRole('Amministratore')")
    public ResponseEntity<String> eliminaFilm(@PathVariable Integer filmId) {
        try {
            filmService.eliminaFilm(filmId);
            return new ResponseEntity<>("Film eliminato con successo.", HttpStatus.OK);
        } catch (MovieNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante l'eliminazione del film.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PutMapping("/stati")
    @PreAuthorize("hasRole('Amministratore')")
    public ResponseEntity<String> aggiornaStatoFilm() {
        try {
            filmService.aggiornaStatoFilmNonRilasciati();
            return new ResponseEntity<>("Lo stato dei film non rilasciati è stato aggiornato con successo.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante l'aggiornamento dello stato dei film", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/{filmId}/prenota")
    @PreAuthorize("hasRole('Utente') or hasRole('Amministratore')")
    public ResponseEntity<String> prenotaAnteprimaFilm(@PathVariable Integer filmId, @RequestParam int quantita) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            filmService.prenotaPostiAnteprimaFilm(utenteId,filmId,quantita);
            return new ResponseEntity<>("Prenotazione effettuata con successo.", HttpStatus.OK);
        }
        catch (UserNotFoundException | MovieNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
        catch (InvalidBookingException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la prenotazione dei posti per l'anteprima.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("/{filmId}/valuta")
    @PreAuthorize("hasRole('Utente') or hasRole('Amministratore')")
    public ResponseEntity<String> valutaFilm(@PathVariable Integer filmId, @RequestParam Integer valutazione) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            filmService.valutaFilm(utenteId,filmId,valutazione);
            return new ResponseEntity<>("La tua valutazione è stata aggiunta con successo.", HttpStatus.OK);
        }
        catch (IllegalRatingException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
        catch (WatchableAlreadyRatedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        catch (UserNotFoundException | MovieNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la valutazione del film.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/{filmId}/valutazione")
    @PreAuthorize("hasRole('Utente') or hasRole('Amministratore')")
    public ResponseEntity<String> rimuoviValutazione(@PathVariable Integer filmId) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            filmService.rimuoviValutazione(utenteId,filmId);
            return new ResponseEntity<>("La tua valutazione è stata rimossa con successo.", HttpStatus.OK);
        }
        catch (UserNotFoundException | MovieNotFoundException | RatingNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la rimozione della tua valutazione del film.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    private Integer getUtenteLoggatoId() throws UserNotFoundException {
        String nomeUtente = JwtUtils.getNomeUtente();
        return utenteService.getUtente(nomeUtente).getUtenteId();
    }








}
