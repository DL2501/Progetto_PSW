package ProjectPSW.controllers;

import ProjectPSW.entities.Utente;
import ProjectPSW.services.UtenteService;
import ProjectPSW.support.exceptions.UserMailAlreadyExistException;
import ProjectPSW.support.exceptions.UserNameAlreadyExistException;
import ProjectPSW.support.exceptions.UserNotFoundException;
import ProjectPSW.support.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/utenti")
public class UtenteController {

    @Autowired
    private UtenteService utenteService;


    @PostMapping
    public ResponseEntity<String> registraUtente(@RequestBody Utente utente) {
        try {
            utenteService.registraUtente(utente);
            return new ResponseEntity<>("Registrazione completata con successo.", HttpStatus.CREATED);
        } catch (UserNameAlreadyExistException | UserMailAlreadyExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
        catch (Exception e) {
            return new ResponseEntity<>("Errore durante la registrazione dell'utente.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{utenteId}")
    @PreAuthorize("hasRole('Amministratore')")
    public ResponseEntity<?> getUtente(@PathVariable Integer utenteId) {
        try {
            Utente utente = utenteService.getUtente(utenteId);
            return new ResponseEntity<>(utente, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la visualizazzione dell'account.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/profilo")
    @PreAuthorize("hasRole('Utente') or hasRole('Amministratore)")
    public ResponseEntity<?> getProfilo() {
        try {
            String nomeUtenteAutenticato = JwtUtils.getNomeUtente();
            Utente utente = utenteService.getUtente(nomeUtenteAutenticato);
            return new ResponseEntity<>(utente, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante la visualizazzione dell'account.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @DeleteMapping("/profilo")
    @PreAuthorize("hasRole('Utente') or hasRole('Amministratore)")
    public ResponseEntity<String> eliminaAccount() {
        try {
            String nomeUtenteAutenticato = JwtUtils.getNomeUtente();
            Integer utenteId = utenteService.getUtente(nomeUtenteAutenticato).getUtenteId();
            utenteService.eliminaAccount(utenteId);
            return new ResponseEntity<>("Account eliminato con successo.", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Errore durante l'eliminazione dell'account.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }







}
