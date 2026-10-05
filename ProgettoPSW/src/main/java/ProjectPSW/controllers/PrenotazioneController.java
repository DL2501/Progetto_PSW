package ProjectPSW.controllers;

import ProjectPSW.entities.Prenotazione;
import ProjectPSW.services.PrenotazioneService;
import ProjectPSW.services.UtenteService;
import ProjectPSW.support.exceptions.BookingNotFoundException;
import ProjectPSW.support.exceptions.MovieNotFoundException;
import ProjectPSW.support.exceptions.UserNotFoundException;
import ProjectPSW.support.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/prenotazioni")
@PreAuthorize("hasRole('Utente') or hasRole('Amministratore')")
public class PrenotazioneController {

    @Autowired
    private UtenteService utenteService;

    @Autowired
    private PrenotazioneService prenotazioneService;


    @GetMapping("/{filmId}")
    public ResponseEntity<?> getPrenotazione(@PathVariable Integer filmId) {
        try {
            Integer utenteId = getUtenteLoggatoId();
            Prenotazione prenotazione = prenotazioneService.getPrenotazione(utenteId,filmId);
            return new ResponseEntity<>(prenotazione, HttpStatus.OK);
        }
        catch (UserNotFoundException | BookingNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la visualizzazione della prenotazione selezionata.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping
    public ResponseEntity<?> getListaPrenotazioni() {
        try {
            Integer utenteId = getUtenteLoggatoId();
            List<Prenotazione> prenotazioniUtente = prenotazioneService.mostraPrenotazioniUtente(utenteId);
            return new ResponseEntity<>(prenotazioniUtente, HttpStatus.OK);
        }
        catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la visualizzazione della lista delle tue prenotazioni.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{filmId}/numero-prenotazioni")
    public ResponseEntity<?> getNumeroPrenotazioni(@PathVariable Integer filmId) {
        try {
            int numeroPrenotazioniFilm = prenotazioneService.getNumeroPrenotazioniFilm(filmId);
            return new ResponseEntity<>(numeroPrenotazioniFilm, HttpStatus.OK);
        }
        catch (MovieNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la visualizzazione del numero totale di prenotazioni effettuate per l'anteprima del film selezionato.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }




    private Integer getUtenteLoggatoId() throws UserNotFoundException {
        String nomeUtente = JwtUtils.getNomeUtente();
        return utenteService.getUtente(nomeUtente).getUtenteId();
    }




}

