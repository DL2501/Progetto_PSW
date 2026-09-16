package ProjectPSW.services;

import ProjectPSW.entities.Prenotazione;
import ProjectPSW.repositories.FilmRepository;
import ProjectPSW.repositories.PrenotazioneRepository;
import ProjectPSW.repositories.UtenteRepository;
import ProjectPSW.support.exceptions.BookingNotFoundException;
import ProjectPSW.support.exceptions.MovieNotFoundException;
import ProjectPSW.support.exceptions.UserNotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PrenotazioneService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private FilmRepository filmRepository;


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Prenotazione getPrenotazione(@NonNull Integer prenotazioneId) throws BookingNotFoundException {
        Optional<Prenotazione> prenotazioneOpt = prenotazioneRepository.findById(prenotazioneId);
        if (prenotazioneOpt.isEmpty())
            throw new BookingNotFoundException("La prenotazione selezionata non è presente nel registro delle prenotazioni");
        return prenotazioneOpt.get();
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Prenotazione getPrenotazioneByCodice(@NonNull String codiceBiglietto) throws BookingNotFoundException {
        Optional<Prenotazione> prenotazioneOpt = prenotazioneRepository.findByCodiceBiglietto(codiceBiglietto);
        if (prenotazioneOpt.isEmpty())
            throw new BookingNotFoundException("La prenotazione selezionata con codice biglietto: " + codiceBiglietto + ", non è presente nel registro delle prenotazioni");
        return prenotazioneOpt.get();
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<Prenotazione> mostraPrenotazioniUtente(@NonNull Integer utenteId) throws UserNotFoundException {
        if (!(utenteRepository.existsById(utenteId)))
            throw new UserNotFoundException("Errore: l'utente del quale si desidera soddisfare questa richiesta è inesistente.");
        return prenotazioneRepository.findByUtenteIdOrderByDataPrenotazioneDesc(utenteId);
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public int getNumeroPrenotazioniFilm(@NonNull Integer filmId) throws MovieNotFoundException {
        if (!(filmRepository.existsById(filmId)))
            throw new MovieNotFoundException("Errore: non esistono prenotazoni effettuate per un film che non è presente all'interno del catalogo.");
        return prenotazioneRepository.countByFilmIdAndValidaTrue(filmId);






    }





























}
