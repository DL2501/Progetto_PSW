package ProjectPSW.services;

import ProjectPSW.entities.Film;
import ProjectPSW.entities.Utente;
import ProjectPSW.entities.Visione_Watchable;
import ProjectPSW.entities.Watchable;
import ProjectPSW.repositories.*;
import ProjectPSW.support.exceptions.UserMailAlreadyExistException;
import ProjectPSW.support.exceptions.UserNameAlreadyExistException;
import ProjectPSW.support.exceptions.UserNotFoundException;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UtenteService {

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private VisioneWatchableRepository visioneRepository;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private EpisodioRepository episodioRepository;


    @Transactional(rollbackFor = Exception.class)
    public void registraUtente(@NonNull Utente u) throws UserMailAlreadyExistException, UserNameAlreadyExistException {
        String nomeUtente = u.getNomeUtente();
        String email = u.getEmail();
        if (utenteRepository.existsByNomeUtente(nomeUtente))
            throw new UserNameAlreadyExistException("Il nome utente scelto per questo account è già in uso. Per procedere alla registrazione scegli una nuovo nome utente.");
        if (utenteRepository.existsByEmail(email))
            throw new UserMailAlreadyExistException("L'email scelta per questo account è già in uso. Per procedere alla registrazione scegli una nuova email.");
        utenteRepository.save(u);
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Utente getUtente(@NonNull Integer utenteId) throws UserNotFoundException {
        Optional<Utente> utenteOpt = utenteRepository.findById(utenteId);
        if (utenteOpt.isEmpty())
            throw new UserNotFoundException("L'utente non è stato registrato.");
        return utenteOpt.get();
    }


    @Transactional(rollbackFor = Exception.class)
    public void eliminaAccount(@NonNull Integer utenteId) throws UserNotFoundException {
        if (utenteRepository.findByIdForUpdate(utenteId).isEmpty())
            throw new UserNotFoundException("Impossibile eliminare un utente non registrato.");
        List<Visione_Watchable> valutazioniUtente = visioneRepository.raccogliValutazioniUtente(utenteId);
        for (Visione_Watchable vw : valutazioniUtente) {
            Watchable w = vw.getWatchable();
            Integer watchableId = w.getWatchableId();
            Integer valutazione = vw.getValutazione();
            if (w instanceof Film)
                filmRepository.rimuoviVoto(watchableId,valutazione);
            else
                episodioRepository.rimuoviVoto(watchableId,valutazione);
        }
        prenotazioneRepository.eliminaPrenotazioniUtente(utenteId);
        visioneRepository.eliminaVideotecaUtente(utenteId);
        utenteRepository.deleteById(utenteId);
    }





























}
