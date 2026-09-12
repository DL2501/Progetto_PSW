package ProjectPSW.services;

import ProjectPSW.entities.*;
import ProjectPSW.repositories.*;
import ProjectPSW.support.Exceptions.*;
import ProjectPSW.support.Utility.ricercaDTO.FiltriRicercaCatalogoVideotecaDTO;
import ProjectPSW.support.Utility.ricercaDTO.FiltriRicercaFilmVideotecaDTO;
import ProjectPSW.support.Utility.ricercaDTO.FiltriRicercaSerieTVVideotecaDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
public class VisioneWatchableService {

    @Autowired
    private VisioneWatchableRepository visioneRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private SerieTVRepository serieTVRepository;

    @Autowired
    private UtenteRepository utenteRepository;

    @Autowired
    private EpisodioRepository episodioRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Page<Visione_Watchable> ricercaBaseVideoteca(@NonNull Integer utenteId, FiltriRicercaCatalogoVideotecaDTO filtri) throws UserNotFoundException {
        if (!(utenteRepository.existsById(utenteId)))
            throw new UserNotFoundException("Errore: l'account in questione e la videoteca ad esso associata sono entrambi inesistenti.");
        String titolo = filtri.getTitolo();
        String genere = filtri.getGenere();
        Integer annoUscita = filtri.getAnnoUscita();
        Integer valutazione = filtri.getValutazione();
        StatoVisione statoVisione = filtri.getStatoVisione();
        int numeroPagina = filtri.getNumeroPagina();
        int elementiPerPagina = filtri.getElementiPerPagina();
        Pageable richiestaPaginazione = PageRequest.of(numeroPagina,elementiPerPagina);
        return visioneRepository.ricercaDinamicaBaseVP(utenteId,titolo,genere,annoUscita,statoVisione,valutazione,richiestaPaginazione);
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Page<Visione_Watchable> ricercaSezioneFilmVideoteca(@NonNull Integer utenteId, FiltriRicercaFilmVideotecaDTO filtri) throws UserNotFoundException {
        if (!(utenteRepository.existsById(utenteId)))
            throw new UserNotFoundException("Errore: l'account in questione e la videoteca ad esso associata sono entrambi inesistenti.");
        String titolo = filtri.getTitolo();
        String genere = filtri.getGenere();
        Integer annoUscita = filtri.getAnnoUscita();
        Integer valutazione = filtri.getValutazione();
        StatoVisione statoVisione = filtri.getStatoVisione();
        Integer durata = filtri.getDurata();
        String regista = filtri.getRegista();
        int numeroPagina = filtri.getNumeroPagina();
        int elementiPerPagina = filtri.getElementiPerPagina();
        Pageable richiestaPaginazione = PageRequest.of(numeroPagina,elementiPerPagina);
        return visioneRepository.ricercaDinamicaFilmVP(utenteId,titolo,genere,annoUscita,durata,regista,statoVisione,valutazione,richiestaPaginazione);
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Page<Visione_Watchable> ricercaSezioneSerieTVVideoteca(@NonNull Integer utenteId, FiltriRicercaSerieTVVideotecaDTO filtri) throws UserNotFoundException {
        if (!(utenteRepository.existsById(utenteId)))
            throw new UserNotFoundException("Errore: l'account in questione e la videoteca ad esso associata sono entrambi inesistenti.");
        String titolo = filtri.getTitolo();
        String genere = filtri.getGenere();
        Integer annoUscita = filtri.getAnnoUscita();
        Integer valutazione = filtri.getValutazione();
        StatoVisione statoVisione = filtri.getStatoVisione();
        Integer episodi = filtri.getNumeroEpisodi();
        String ideatore = filtri.getIdeatore();
        Integer annoConclusione = filtri.getAnnoConclusione();
        StatoSerieTV stato = filtri.getStato();
        int numeroPagina = filtri.getNumeroPagina();
        int elementiPerPagina = filtri.getElementiPerPagina();
        Pageable richiestaPaginazione = PageRequest.of(numeroPagina,elementiPerPagina);
        return visioneRepository.ricercaDinamicaSerieTVVP(utenteId,titolo,genere,annoUscita,episodi,ideatore,annoConclusione,stato,statoVisione,valutazione,richiestaPaginazione);
    }


    @Transactional(rollbackFor = Exception.class)
    public void aggiungiFilmAllaVideoteca(@NonNull Integer utenteId, @NonNull Integer filmId, @NonNull StatoVisione statoVisione, Integer valutazione) throws UserNotFoundException, MovieNotFoundException, IllegalMovieStateException, IllegalRatingException, WatchableAlreadyInLibraryException {
        if (valutazione != null && (valutazione < 1 || valutazione > 5))
            throw new IllegalRatingException("Errore: il voto deve essere necessariamente compreso tra 1 e 5 stelle.");
        Utente utente = utenteRepository.findByIdForUpdate(utenteId).orElseThrow(() -> new UserNotFoundException("Impossibile aggiungere il contenuto alla videoteca: l'utente risulta inesistente."));
        Film film = filmRepository.findById(filmId).orElseThrow(() -> new MovieNotFoundException("Impossibile aggiungere il film alla videoteca: il film non risulta presente all'interno del catalogo"));
        if (film.getStato() != StatoFilm.RILASCIATO)
            throw new IllegalMovieStateException("Errore: un film non rilasciato non può essere aggiunto alla propria videoteca personale.");
        Optional<Visione_Watchable> vwOpt = visioneRepository.findByUtenteIdAndWatchableIdForUpdate(utenteId,filmId);
        if (vwOpt.isPresent()) {
            Visione_Watchable vw = vwOpt.get();
            if (vw.isInVideoteca())
                throw new WatchableAlreadyInLibraryException("Errore: il film che si desidera aggiungere è già presente all'interno della videoteca personale.");
            vw.setInVideoteca(true);
            vw.setStatoVisione(statoVisione);
        }
        else {
            Visione_Watchable nuovoVW = new Visione_Watchable(utente,film,statoVisione,valutazione);
            visioneRepository.save(nuovoVW);
            if (valutazione != null) {
                int valutazioniModificate = filmRepository.aggiungiVoto(utenteId,filmId);
                if (valutazioniModificate == 0)
                    throw new NotRatableMovieException("Impossibile valutare: il film selezionato non è ancora uscito nelle sale o non è presente nel catalogo.");
            }
        }
    }



    @Transactional(rollbackFor = Exception.class)
    public void aggiungiSerieTVAllaVideoteca(@NonNull Integer utenteId, @NonNull Integer serieTvId, @NonNull StatoVisione statoVisione, Integer valutazione, Integer episodiVisti, boolean episodioSuccessivoInVisione) throws IllegalRatingException, IllegalLibraryContentStateException, UserNotFoundException, TVSeriesNotFoundException, IllegalTVSeriesStateException {
        if (valutazione != null && (valutazione < 1 || valutazione > 5))
            throw new IllegalRatingException("Errore: il voto deve essere necessariamente compreso tra 1 e 5 stelle.");
        if ((statoVisione != StatoVisione.IN_VISIONE && (episodiVisti != null || episodioSuccessivoInVisione)))
            throw new IllegalLibraryContentStateException("Errore: lo stato di visione impostato è inconsistente con il resto dei dati specificati.");
        Utente utente = utenteRepository.findById(utenteId).orElseThrow(() -> new UserNotFoundException("Impossibile aggiungere il contenuto alla videoteca: l'utente risulta inesistente."));
        SerieTV serie = serieTVRepository.findById(serieTvId).orElseThrow(() -> new TVSeriesNotFoundException("La serie TV non è presente all'interno del catalogo."));
        if (serie.getStato() == StatoSerieTV.IN_USCITA)
            throw new IllegalTVSeriesStateException("Impossibile aggiungere alla videoteca una serie TV non ancora rilasciata.");
        if (episodiVisti != null && (episodiVisti < 0 || episodiVisti > serie.getNumeroEpisodi() || (episodiVisti.equals(serie.getNumeroEpisodi()) && episodioSuccessivoInVisione)))
            throw new IllegalLibraryContentStateException("Il numero di espisodi visti non può eccedere il numero di episodi di una serie TV o essere negativo");
        if (!(visioneRepository.existsByUtenteIdAndWatchableId(utenteId,serieTvId))) {
            Visione_Watchable serieVW = new Visione_Watchable(utente,serie,statoVisione,valutazione);
            visioneRepository.save(serieVW);
            List<Episodio> episodiSerie = episodioRepository.findBySerieTvIdOrderByStagioneAscNumeroAsc(serieTvId);
            for (int i = 0; i < episodiSerie.size(); i++) {
                Episodio episodio = episodiSerie.get(i);
                Integer episodioId = episodio.getWatchableId();
                StatoVisione statoEpisodio = StatoVisione.DA_VEDERE;
                if (episodiVisti != null) {
                    if ((i+1) <= episodiVisti)
                        statoEpisodio = StatoVisione.VISTO;
                    if (episodioSuccessivoInVisione && (i+1) == (episodiVisti+1))
                        statoEpisodio = StatoVisione.IN_VISIONE;
                }
                Optional<Visione_Watchable> vwOpt = visioneRepository.findByUtenteIdAndWatchableIdForUpdate(utenteId,episodioId);
                if (vwOpt.isPresent()) {
                    Visione_Watchable episodioVW = vwOpt.get();
                    episodioVW.setInVideoteca(true);
                    episodioVW.setStatoVisione(statoEpisodio);
                }
                else {
                    Visione_Watchable nuovoEpisodioVW = new Visione_Watchable(utente,episodio,statoEpisodio,null);
                    visioneRepository.save(nuovoEpisodioVW);
                }
            }
        }
    }



    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Visione_Watchable getVisioneWatchable(@NonNull Integer utenteId, @NonNull Integer watchableId) throws UserNotFoundException, WatchableNotInLibraryException {
        if (!(utenteRepository.existsById(utenteId)))
            throw new UserNotFoundException("Errore: l'account in questione e la videoteca ad esso associata sono entrambi inesistenti.");
        Optional<Visione_Watchable> vwOpt = visioneRepository.findByUtenteIdAndWatchableIdAndInVideotecaTrue(utenteId,watchableId);
        if (vwOpt.isEmpty())
            throw new WatchableNotInLibraryException("Il contenuto selezionato non è presente all'interno della videoteca.");
        return vwOpt.get();
    }



    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Watchable getWatchableDallaVideoteca(@NonNull Integer utenteId, @NonNull Integer watchableId) throws UserNotFoundException, WatchableNotInLibraryException {
        if (!(utenteRepository.existsById(utenteId)))
            throw new UserNotFoundException("Errore: l'account in questione e la videoteca ad esso associata sono entrambi inesistenti.");
        Optional<Visione_Watchable> vwOpt = visioneRepository.findByUtenteIdAndWatchableIdAndInVideotecaTrue(utenteId,watchableId);
        if (vwOpt.isEmpty())
            throw new WatchableNotInLibraryException("Il contenuto selezionato non è presente all'interno della videoteca.");
        Visione_Watchable vw = vwOpt.get();
        Watchable watchable = vw.getWatchable();
        if (watchable instanceof Episodio ep) {
            Integer serieTvId = ep.getSerieTv().getWatchableId();
            if (!(visioneRepository.existsByUtenteIdAndWatchableId(utenteId,serieTvId)))
                throw new WatchableNotInLibraryException("Impossobile visualizzare l'episodio di una serie TV che non è presente all'interno della videoteca.");
        }
        entityManager.refresh(watchable);
        return watchable;
    }



    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Page<Visione_Watchable> sfogliaSezionePrincipaleVideoteca(@NonNull Integer utenteId, Integer numeroPagina) throws UserNotFoundException {
        if (!(utenteRepository.existsById(utenteId)))
            throw new UserNotFoundException("Errore: l'account in questione e la videoteca ad esso associata sono entrambi inesistenti.");
        int paginaVerificata = (numeroPagina != null && numeroPagina >= 0) ? numeroPagina : 0;
        Pageable richiestaPaginazione = PageRequest.of(paginaVerificata,20);
        return visioneRepository.mostraSezionePrincipaleVideoteca(utenteId,richiestaPaginazione);
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Page<Visione_Watchable> sfogliaSezioneFilmVideoteca(@NonNull Integer utenteId, Integer numeroPagina) throws UserNotFoundException {
        if (!(utenteRepository.existsById(utenteId)))
            throw new UserNotFoundException("Errore: l'account in questione e la videoteca ad esso associata sono entrambi inesistenti.");
        int paginaVerificata = (numeroPagina != null && numeroPagina >= 0) ? numeroPagina : 0;
        Pageable richiestaPaginazione = PageRequest.of(paginaVerificata,20);
        return visioneRepository.mostraSezioneFilmVideoteca(utenteId,richiestaPaginazione);
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Page<Visione_Watchable> sfogliaSezioneSerieTVVideoteca(@NonNull Integer utenteId, Integer numeroPagina) throws UserNotFoundException {
        if (!(utenteRepository.existsById(utenteId)))
            throw new UserNotFoundException("Errore: l'account in questione e la videoteca ad esso associata sono entrambi inesistenti.");
        int paginaVerificata = (numeroPagina != null && numeroPagina >= 0) ? numeroPagina : 0;
        Pageable richiestaPaginazione = PageRequest.of(paginaVerificata,20);
        return visioneRepository.mostraSezioneSerieTVVideoteca(utenteId,richiestaPaginazione);
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<Visione_Watchable> getAllEpisodiSerieTVInVideoteca(@NonNull Integer utenteId, @NonNull Integer serieTvId) throws TVSeriesNotFoundException, UnreleasedTVSeriesException, UserNotFoundException, WatchableNotInLibraryException {
        Utente utente = utenteRepository.findById(utenteId).orElseThrow(() -> new UserNotFoundException("Impossibile aggiungere il contenuto alla videoteca: l'utente risulta inesistente."));
        if (!(visioneRepository.existsByUtenteIdAndWatchableId(utenteId,serieTvId)))
            throw new WatchableNotInLibraryException("La serie TV non è presente all'interno della videoteca");
        if (!(serieTVRepository.existsById(serieTvId)))
            throw new TVSeriesNotFoundException("La serie TV non è presente all'interno del catalogo");
        List<Episodio> episodiSerieTV = episodioRepository.findBySerieTvIdOrderByStagioneAscNumeroAsc(serieTvId);
        for (Episodio episodio : episodiSerieTV) {
            Integer episodioId = episodio.getWatchableId();
            if (!(visioneRepository.existsByUtenteIdAndWatchableId(utenteId,episodioId))) {
                Visione_Watchable episodioVW = new Visione_Watchable(utente,episodio,StatoVisione.DA_VEDERE,null);
                visioneRepository.save(episodioVW);
            }
        }
        if (episodiSerieTV.isEmpty())
            throw new UnreleasedTVSeriesException("Impossibile trovare episodi: La serie TV o i suoi episodi sono stati rimosssi dal catalogo.");
        List<Visione_Watchable> episodiSerieTVInVideoteca = visioneRepository.findAllEpisodiSerieTV(utenteId,serieTvId);
        if (episodiSerieTVInVideoteca.isEmpty())
            throw new WatchableNotInLibraryException("Impossibile trovare episodi: La serie TV o i suoi episodi sono stati rimossi dalla Videoteca.");
        return episodiSerieTVInVideoteca;
    }



    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<Visione_Watchable> getEpisodiStagioneSerieTVInVideoteca(@NonNull Integer utenteId, @NonNull Integer serieTvId, @NonNull Integer numeroStagione) throws TVSeriesNotFoundException, SeasonNotFoundException, UserNotFoundException, WatchableNotInLibraryException {
        if (numeroStagione <= 0)
            throw new IllegalArgumentException("Errore: il valore della stagione deve necessariamente essere maggiore di zero.");
        Utente utente = utenteRepository.findById(utenteId).orElseThrow(() -> new UserNotFoundException("Impossibile aggiungere il contenuto alla videoteca: l'utente risulta inesistente."));
        if (!(visioneRepository.existsByUtenteIdAndWatchableId(utenteId,serieTvId)))
            throw new WatchableNotInLibraryException("La serie TV non è presente all'interno della videoteca");
        if (!(serieTVRepository.existsById(serieTvId)))
            throw new TVSeriesNotFoundException("La serie TV non è presente all'interno del catalogo");
        List<Episodio> episodiStagioneSerieTV = episodioRepository.findBySerieTvIdAndStagioneOrderByNumeroAsc(serieTvId,numeroStagione);
        for (Episodio episodio : episodiStagioneSerieTV) {
            Integer episodioId = episodio.getWatchableId();
            if (!(visioneRepository.existsByUtenteIdAndWatchableId(utenteId,episodioId))) {
                Visione_Watchable episodioVW = new Visione_Watchable(utente,episodio,StatoVisione.DA_VEDERE,null);
                visioneRepository.save(episodioVW);
            }
        }
        if (episodiStagioneSerieTV.isEmpty())
            throw new SeasonNotFoundException("Impossibile trovare la stagione: la serie TV specificata non possiede la stagione " + numeroStagione + ". Questo può accadere sia per motivi regolari oppure perché serie TV ed episodi potrebbero essere stati rimossi dal catalogo.");
        List<Visione_Watchable> episodiStagioneSerieTVInVideoteca = visioneRepository.findEpisodiBySerieTVAndStagione(utenteId,serieTvId,numeroStagione);
        if (episodiStagioneSerieTVInVideoteca.isEmpty())
            throw new WatchableNotInLibraryException("Impossibile trovare la stagione: la stagione " + numeroStagione + " della serie TV specificata non è presente all'interno della videoteca. Questo può accadere sia per motivi regolari oppure perché la serie TV o questa specifica stagione sono stati rimossi dalla videoteca.");
        return episodiStagioneSerieTVInVideoteca;
    }



    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public Visione_Watchable getEpisodioByStagioneAndNumeroInVideoteca(@NonNull Integer utenteId, @NonNull Integer serieTvId, @NonNull Integer numeroStagione, @NonNull Integer numeroEpisodio) throws TVSeriesNotFoundException, EpisodeNotFoundException, WatchableNotInLibraryException, UserNotFoundException {
        if (numeroStagione <= 0 || numeroEpisodio <= 0)
            throw new IllegalArgumentException("Errore: il valore della stagione e del numero dell'episodio devono necessariamente essere maggiori di zero.");
        Utente utente = utenteRepository.findById(utenteId).orElseThrow(() -> new UserNotFoundException("Impossibile aggiungere il contenuto alla videoteca: l'utente risulta inesistente."));
        if (!(visioneRepository.existsByUtenteIdAndWatchableId(utenteId,serieTvId)))
            throw new WatchableNotInLibraryException("L'episodio della serie TV selezionata non è presente all'interno della videoteca");
        if (!(serieTVRepository.existsById(serieTvId)))
            throw new TVSeriesNotFoundException("La serie TV non è presente all'interno del catalogo");
        Optional<Episodio> episodioOpt = episodioRepository.findBySerieTvIdAndStagioneAndNumero(serieTvId,numeroStagione,numeroEpisodio);
        if (episodioOpt.isEmpty())
            throw new EpisodeNotFoundException("Episodio non trovato: nella serie TV specificata non esiste l'episodio " + numeroEpisodio + " della stagione " + numeroStagione + ". Questo può accadere sia per motivi regolari oppure perché serie TV, episodi o questo specifico episodio sono stati rimossi dal catalogo.");
        Episodio episodio = episodioOpt.get();
        Integer episodioId = episodio.getWatchableId();
        if (!(visioneRepository.existsByUtenteIdAndWatchableId(utenteId,episodioId))) {
            Visione_Watchable episodioVW = new Visione_Watchable(utente,episodio,StatoVisione.DA_VEDERE,null);
            visioneRepository.save(episodioVW);
        }
        Optional<Visione_Watchable> episodioInVideotecaOpt = visioneRepository.findEpisodioBySerieTVAndStagioneAndNumero(utenteId,serieTvId,numeroStagione,numeroEpisodio);
        if (episodioInVideotecaOpt.isEmpty())
            throw new WatchableNotInLibraryException("Episodio non trovato: all'interno della videoteca nella serie TV specificata non esiste l'episodio " + numeroEpisodio + " della stagione " + numeroStagione + ". Questo può accadere sia per motivi regolari oppure perché serie TV, episodi o questo specifico episodio sono stati rimossi dalla videoteca.");
        return episodioInVideotecaOpt.get();
    }


    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS, rollbackFor = Exception.class)
    public List<Visione_Watchable> mostraValutazioniUtente(@NonNull Integer utenteId) throws UserNotFoundException {
        if (!(utenteRepository.existsById(utenteId)))
            throw new UserNotFoundException("Errore: l'account in questione e la videoteca ad esso associata sono entrambi inesistenti.");
        return visioneRepository.findByUtenteIdAndInVideotecaFalse(utenteId);
    }


    @Transactional(rollbackFor = Exception.class)
    public void impostaStatoVisioneWatchable(@NonNull Integer utenteId, @NonNull Integer watchableId, @NonNull StatoVisione statoVisione) throws UserNotFoundException, WatchableNotInLibraryException {
        if (!(utenteRepository.existsById(utenteId)))
            throw new UserNotFoundException("Errore: l'account in questione e la videoteca ad esso associata sono entrambi inesistenti.");
        Optional<Visione_Watchable> vwOpt = visioneRepository.findByUtenteIdAndWatchableIdForUpdate(utenteId,watchableId);
        if (vwOpt.isEmpty())
            throw new WatchableNotInLibraryException("Il contenuto selezionato non è presente all'interno della videoteca.");
        Visione_Watchable vw = vwOpt.get();
        vw.setStatoVisione(statoVisione);
    }


    @Transactional(rollbackFor = Exception.class)
    public void modificaValutazioneWatchable(@NonNull Integer utenteId, @NonNull Integer watchableId, Integer valutazione) throws IllegalRatingException, UserNotFoundException, WatchableNotInLibraryException, WatchableNotFoundException {
        if (valutazione != null && (valutazione < 1 || valutazione > 5))
            throw new IllegalRatingException("Errore: il voto deve essere necessariamente compreso tra 1 e 5 stelle.");
        Utente utente = utenteRepository.findByIdForUpdate(utenteId).orElseThrow(() -> new UserNotFoundException("Errore: utente non trovato."));
        Visione_Watchable vwDaValutare = visioneRepository.findByUtenteIdAndWatchableIdForUpdate(utenteId,watchableId).orElseThrow(() -> new WatchableNotInLibraryException("Il contenuto che si desidera valutare non è presente all'interno della videoteca."));
        Watchable watchable = vwDaValutare.getWatchable();
        if (!(watchable instanceof SerieTV)) {
            Integer vecchiaValutazione = vwDaValutare.getValutazione();
            if (valutazione != null) {
                if (vwDaValutare.getValutazione() != null) {
                    int valutazioniRimosse, valutazioniModificate;
                    if (watchable instanceof Film) {
                        valutazioniRimosse = filmRepository.rimuoviVoto(watchableId,vecchiaValutazione);
                        valutazioniModificate = filmRepository.aggiungiVoto(watchableId,valutazione);
                    }
                    else {
                        valutazioniRimosse = episodioRepository.rimuoviVoto(watchableId,vecchiaValutazione);
                        valutazioniModificate = episodioRepository.aggiungiVoto(watchableId,valutazione);
                    }
                    if (valutazioniRimosse == 0 || valutazioniModificate == 0)
                        throw new WatchableNotFoundException("Impossibile modificare il voto: il watchable in questione non è presente all'interno del catalogo.");
                }
                else {
                    int valuatzioniAggiunte;
                    if (watchable instanceof Film)
                        valuatzioniAggiunte = filmRepository.aggiungiVoto(watchableId,valutazione);
                    else
                        valuatzioniAggiunte = episodioRepository.aggiungiVoto(watchableId,valutazione);
                    if (valuatzioniAggiunte == 0)
                        throw new WatchableNotFoundException("Impossibile aggiungere il voto: il watchable che si desidera valutare non è presente all'interno del catalogo.");
                }
            }
            else {
                if (vwDaValutare.getValutazione() != null) {
                    int valutazioniRimosse;
                    if (watchable instanceof Film)
                        valutazioniRimosse = filmRepository.rimuoviVoto(watchableId,vecchiaValutazione);
                    else
                        valutazioniRimosse = episodioRepository.rimuoviVoto(watchableId,vecchiaValutazione);
                    if (valutazioniRimosse == 0)
                        throw new WatchableNotFoundException("Impossibile rimuovere il voto: il watchable in questione non è presente all'interno del catalogo");
                }
            }
        }
        vwDaValutare.impostaValutazione(valutazione);
        if (!(vwDaValutare.isInVideoteca()) && vwDaValutare.getValutazione() == null)
            visioneRepository.delete(vwDaValutare);
    }



    @Transactional(rollbackFor = Exception.class)
    public void rimuoviFilmDallaVideoteca(@NonNull Integer utenteId, @NonNull Integer filmId) throws UserNotFoundException, MovieNotFoundException, WatchableNotInLibraryException {
        if (!(utenteRepository.existsById(utenteId)))
            throw new UserNotFoundException("Errore: l'account in questione e la videoteca ad esso associata sono entrambi inesistenti.");
        if (!(filmRepository.existsById(filmId)))
            throw new MovieNotFoundException("Errore: il film che si desidera rimuovere non è presente nel catalogo nè tantomeno all'interno della videoteca.");
        Visione_Watchable vwDaRimuovere = visioneRepository.findByUtenteIdAndWatchableIdAndInVideotecaTrueForUpdate(utenteId,filmId).orElseThrow(() -> new WatchableNotInLibraryException("Errore: impossibile rimuovere un film che non è contenuto all'interno della videoteca"));
        if (vwDaRimuovere.getValutazione() != null) {
            vwDaRimuovere.setStatoVisione(null);
            vwDaRimuovere.setInVideoteca(false);
        }
        else
            visioneRepository.delete(vwDaRimuovere);
    }



    @Transactional(rollbackFor = Exception.class)
    public void rimuoviSerieTVDallaVideoteca(@NonNull Integer utenteId, @NonNull Integer serieTvId) throws UserNotFoundException, TVSeriesNotFoundException, WatchableNotInLibraryException {
        if (!(utenteRepository.existsById(utenteId)))
            throw new UserNotFoundException("Errore: l'account in questione e la videoteca ad esso associata sono entrambi inesistenti.");
        if (!(serieTVRepository.existsById(serieTvId)))
            throw new TVSeriesNotFoundException("Errore: la serie TV che si desidera rimuovere non è presente nel catalogo nè tantomeno all'interno della videoteca.");
        Visione_Watchable vwDaRimuovere = visioneRepository.findByUtenteIdAndWatchableIdForUpdate(utenteId,serieTvId).orElseThrow(() -> new WatchableNotInLibraryException("Errore: impossibile rimuovere una serie TV che non è contenuta all'interno della videoteca"));
        List<Visione_Watchable> episodiDaRimuovere = visioneRepository.findAllEpisodiSerieTV(utenteId,serieTvId);
        for (Visione_Watchable episodioVW : episodiDaRimuovere) {
            Integer visioneWatchableId = episodioVW.getVisioneWatchableId();
            if (visioneRepository.findByIdForUpdate(visioneWatchableId).isPresent()) {
                if (episodioVW.getValutazione() != null) {
                    episodioVW.setStatoVisione(null);
                    episodioVW.setInVideoteca(false);
                }
                else
                    visioneRepository.delete(episodioVW);
            }
        }
        visioneRepository.delete(vwDaRimuovere);
    }


    @Transactional(rollbackFor = Exception.class)
    public void svuotaVideoteca(@NonNull Integer utenteId) throws UserNotFoundException {
        if (!(utenteRepository.existsById(utenteId)))
            throw new UserNotFoundException("Errore: l'account in questione e la videoteca ad esso associata sono entrambi inesistenti.");
        List<Visione_Watchable> watchablesInVideoteca = visioneRepository.findByUtenteIdAndInVideotecaTrue(utenteId);
        for (Visione_Watchable watchableInVideoteca : watchablesInVideoteca) {
            Integer visioneWatchableId = watchableInVideoteca.getVisioneWatchableId();
            if (visioneRepository.findByIdForUpdate(visioneWatchableId).isPresent()) {
                if (watchableInVideoteca.getValutazione() != null) {
                    watchableInVideoteca.setStatoVisione(null);
                    watchableInVideoteca.setInVideoteca(false);
                }
                else
                    visioneRepository.delete(watchableInVideoteca);
            }
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void eliminaVideoteca(@NonNull Integer utenteId) throws UserNotFoundException {
        if (!(utenteRepository.existsById(utenteId)))
            throw new UserNotFoundException("Errore: l'account in questione e la videoteca ad esso associata sono entrambi inesistenti.");
        visioneRepository.eliminaVideotecaUtente(utenteId);
    }




    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void aggiornaValutazioneSerieTVByMedia(@NonNull Integer utenteId, @NonNull Integer serieTvId) throws UserNotFoundException, TVSeriesNotFoundException, WatchableNotInLibraryException {
        if (!(utenteRepository.existsById(utenteId)))
            throw new UserNotFoundException("Errore: l'account in questione e la videoteca ad esso associata sono entrambi inesistenti.");
        if (!(serieTVRepository.existsById(serieTvId)))
            throw new TVSeriesNotFoundException("Errore: la serie TV che si desidera rimuovere non è presente nel catalogo nè tantomeno all'interno della videoteca.");
        if (visioneRepository.existsByUtenteIdAndWatchableId(utenteId,serieTvId))
            throw new WatchableNotInLibraryException("Il contenuto selezionato non è presente all'interno della videoteca.");
        visioneRepository.aggiornaValutazioneByMediaSerieTV(utenteId,serieTvId);
    }






}
