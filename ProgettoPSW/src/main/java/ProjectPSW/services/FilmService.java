package ProjectPSW.services;

import ProjectPSW.entities.*;
import ProjectPSW.repositories.FilmRepository;
import ProjectPSW.repositories.PrenotazioneRepository;
import ProjectPSW.repositories.UtenteRepository;
import ProjectPSW.repositories.VisioneWatchableRepository;
import ProjectPSW.support.Exceptions.*;
import ProjectPSW.support.Utility.ricercaDTO.FiltriRicercaFilmDTO;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FilmService {

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private PrenotazioneRepository prenotazioneRepository;

    @Autowired
    private VisioneWatchableRepository visioneRepository;


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<Film> ricercaSezioneFilm(FiltriRicercaFilmDTO filtri) {
        String titolo = filtri.getTitolo();
        String genere = filtri.getGenere();
        Integer annoUscita = filtri.getAnnoUscita();
        Integer valutazione = filtri.getValutazione();
        Integer durata = filtri.getDurata();
        String regista = filtri.getRegista();
        StatoFilm stato = filtri.getStato();
        boolean soloFilmPrenotabili = filtri.isSoloFilmPrenotabili();
        int numeroPagina = filtri.getNumeroPagina();
        int elementiPerPagina = filtri.getElementiPerPagina();
        Pageable richiestaPaginazione = PageRequest.of(numeroPagina,elementiPerPagina);
        return filmRepository.ricercaDinamicaFilm(titolo,genere,annoUscita,valutazione,durata,regista,stato,soloFilmPrenotabili,richiestaPaginazione);
    }


    @Transactional(rollbackFor = Exception.class)
    public void aggiungiFilm(@NonNull Film f) throws MovieAlreadyExistException {
        String imdbId = f.getImdbId();
        if (filmRepository.existsByImdbId(imdbId))
            throw new MovieAlreadyExistException("Il film che stai provando ad inserire è già presente nel catalogo.");
        filmRepository.save(f);
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Film getFilm(@NonNull Integer filmId) throws MovieNotFoundException {
        Optional<Film> filmOpt = filmRepository.findById(filmId);
        if (filmOpt.isEmpty())
            throw new MovieNotFoundException("Il film non è presente all'interno del catalogo.");
        return filmOpt.get();
    }


    @Transactional(propagation = Propagation.NESTED, rollbackFor = Exception.class)
    public Prenotazione prenotaPostiAnteprimaFilm(@NonNull Integer utenteId, @NonNull Integer filmId, int quantita) throws MovieNotFoundException, UserNotFoundException, InvalidBookingException {
        Utente utente = utenteRepository.findByIdForUpdate(utenteId).orElseThrow(() -> new UserNotFoundException("Errore: utente non trovato."));
        Film film = filmRepository.findByIdForUpdate(filmId).orElseThrow(() -> new MovieNotFoundException("Errore: il film non è presente all'interno del catalogo."));
        film.prenotaPostiAnteprima(quantita);
        Prenotazione prenotazione = new Prenotazione(utente,film);
        return prenotazioneRepository.save(prenotazione);
    }


    @Transactional(rollbackFor = Exception.class)
    public void valutaFilm(@NonNull Integer utenteId, @NonNull Integer filmId, @NonNull Integer valutazione) throws MovieNotFoundException, WatchableAlreadyRatedException, UserNotFoundException, IllegalRatingException {
        if (valutazione < 0 || valutazione > 5)
            throw new IllegalRatingException("Errore: il voto deve essere necessariamente compreso tra 1 e 5 stelle.");
        Utente utente = utenteRepository.findByIdForUpdate(utenteId).orElseThrow(() -> new UserNotFoundException("Errore: utente non trovato."));
        Film film = filmRepository.findById(filmId).orElseThrow(() -> new MovieNotFoundException("Errore: Il film non è presente all'interno del catalogo."));
        if (film.getStato() != StatoFilm.RILASCIATO)
            throw new NotRatableMovieException("Impossibile valutare: il film selezionato non è ancora uscito nelle sale.");
        Optional<Visione_Watchable> vwOpt = visioneRepository.findByUtenteIdAndWatchableIdForUpdate(utenteId,filmId);
        if (vwOpt.isPresent()) {
            Visione_Watchable vw = vwOpt.get();
            if (vw.getValutazione() != null)
                throw new WatchableAlreadyRatedException("Impossibile valutare: uno stesso utente non può valutare lo stesso contenuto più di una volta.");
            vw.impostaValutazione(valutazione);
        }
        else {
            Visione_Watchable nuovoVW = new Visione_Watchable(utente,film,valutazione);
            visioneRepository.save(nuovoVW);
        }
        int valutazioniModificate = filmRepository.aggiungiVoto(filmId,valutazione);
        if (valutazioniModificate == 0)
            throw new NotRatableMovieException("Impossibile valutare: il film selezionato non è ancora uscito nelle sale o non è presente nel catalogo.");
    }



    @Transactional(rollbackFor = Exception.class)
    public void rimuoviValutazione(@NonNull Integer utenteId, @NonNull Integer filmId) throws UserNotFoundException, MovieNotFoundException, RatingNotFoundException {
        if (utenteRepository.findByIdForUpdate(utenteId).isEmpty())
            throw new UserNotFoundException("Errore: utente non trovato.");
        if (!(filmRepository.existsById(filmId)))
            throw new MovieNotFoundException("Errore: Il film non è presente all'interno del catalogo.");
        Visione_Watchable vwDaRimuovere = visioneRepository.findByUtenteIdAndWatchableIdAndValutazioneNotNullForUpdate(utenteId,filmId).orElseThrow(() -> new RatingNotFoundException("La valutazione da rimuovere è inesistente."));
        Integer valutazioneDaRimuovere = vwDaRimuovere.getValutazione();
        visioneRepository.delete(vwDaRimuovere);
        int valutazioniModificate = filmRepository.rimuoviVoto(filmId,valutazioneDaRimuovere);
        if (valutazioniModificate == 0)
            throw new NotRatableMovieException("Impossibile rimuovere la valutazione: il film selezionato potrebbe non essere nel catalogo, non essere stato rilasciato o non avere affatto valutazioni.");
    }




    @Transactional(rollbackFor = Exception.class)
    public void eliminaFilm(@NonNull Integer filmId) throws MovieNotFoundException {
        if (filmRepository.findByIdForUpdate(filmId).isEmpty())
            throw new MovieNotFoundException("Errore: non è possibile cancellare un film che non è presente nel catalogo.");
        prenotazioneRepository.eliminaPrenotazioniFilm(filmId);
        visioneRepository.eliminaWatchableDalleVideoteche(filmId);
        filmRepository.deleteById(filmId);
    } // Ci sarebbe da aggiungere un blocco per peremttere il rimborso delle prenotazioni. Tuttavia visto che questa è un operazione estremamente rara, la scrittura del blocco verrà momentaneamente rimandata.




    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 120)
    public void aggiornaStatoFilmNonRilasciati() {
        List<Film> filmDaAggiornare = filmRepository.trovaFilmDaAggiornare();
        for (Film f : filmDaAggiornare) {
            try {
                f.aggiornaStato();
                System.out.println("Il film " + f.getTitolo() + " è stato aggiornato con successo.");
            } catch (RuntimeException e) {
                System.out.println("Il film " + f.getTitolo() + " non è stato aggiornato.");
            }
        }
    }



    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public Page<Film> sfogliaSezioneFilm(Integer numeroPagina) {
        int paginaVerificata = (numeroPagina != null && numeroPagina >= 0) ? numeroPagina : 0;
        Pageable richiestaPaginazione = PageRequest.of(paginaVerificata,20);
        return filmRepository.findAll(richiestaPaginazione);
    }












































}
